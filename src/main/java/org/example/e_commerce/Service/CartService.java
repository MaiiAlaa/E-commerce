package org.example.e_commerce.Service;

import jakarta.transaction.Transactional;
import org.example.e_commerce.Entity.*;
import org.example.e_commerce.Repository.*;
import org.example.e_commerce.dto.dtoRequest.PurchaseRequestDTO;
import org.example.e_commerce.dto.dtoResponse.CartResponseDTO;
import org.example.e_commerce.dto.dtoResponse.SignUpResponseDTO;
import org.example.e_commerce.dto.dtoResponse.cartProductDetailsDTO;
import org.example.e_commerce.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
public class CartService {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartDetailsRepo cartDetailsRepo;

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public CartResponseDTO viewCart(String token) {
        String username = jwtUtil.extractUsername(token);
        Optional<User> userOptional = userRepo.findByUsername(username);

        if (userOptional.isEmpty()) {
            return new CartResponseDTO("User not found with username: " + username, HttpStatus.NOT_FOUND.value(), null, 0.0, 0);
        }

        User user = userOptional.get();
        Long userId = user.getUserid();
        Cart cart = cartRepo.findByUserid(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        // Retrieve all cart details
        List<CartDetails> cartDetailsList = cartDetailsRepo.findByCart(cart);

        // Map CartDetails to a DTO and calculate total price and cart size
        final double[] totalCartPrice = {0.0};
        int cartSize = cartDetailsList.size();  // Calculate cart size
        List<cartProductDetailsDTO> products = cartDetailsList.stream()
                .map(cartDetails -> {
                    // Explicitly fetching the product details
                    Product product = productRepo.findById(cartDetails.getProduct().getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found"));

                    int quantity = cartDetails.getQuantity();
                    double itemTotalPrice = product.getPrice() * quantity; // Calculate item total price

                    totalCartPrice[0] += itemTotalPrice; // Add to total cart price

                    return new cartProductDetailsDTO(
                            product.getProductId(),
                            product.getProductName(),
                            product.getImageUrl(), // Assuming imageUrl holds the main image
                            product.getPrice(),
                            quantity,
                            itemTotalPrice,
                            cartSize // Adding cart size
                    );
                }).toList();

        // Create a response with additional data
        return new CartResponseDTO("Cart retrieved successfully", HttpStatus.OK.value(), products, totalCartPrice[0], cartSize);
    }


    @Transactional
    public SignUpResponseDTO removeProductFromCart(String token, Long productId) {
        String username = jwtUtil.extractUsername(token);
        Optional<User> userOptional = userRepo.findByUsername(username);

        if (userOptional.isEmpty()) {
            return new SignUpResponseDTO("User not found with username: " + username, HttpStatus.NOT_FOUND.value());
        }

        User user = userOptional.get();
        Long userId = user.getUserid();
        Cart cart = cartRepo.findByUserid(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartDetails> existingCartDetails = cartDetailsRepo.findByCartAndProduct(cart, product);

        if (existingCartDetails.isEmpty()) {
            return new SignUpResponseDTO("Product not found in the cart", HttpStatus.NOT_FOUND.value());
        }

        CartDetails cartDetails = existingCartDetails.get();
        cartRepo.findByUserid(userId).ifPresent(c -> {
            if (cartDetails.getQuantity() > 0) {
                // Increase the stock quantity for the product
                product.setStockQuantity(product.getStockQuantity() + cartDetails.getQuantity());
                productRepo.save(product);
            }
            cartDetailsRepo.delete(cartDetails);
        });

        return new SignUpResponseDTO("Product removed from cart", HttpStatus.OK.value());
    }


    @Transactional
    public SignUpResponseDTO addToCart(String token, PurchaseRequestDTO request) {
        String username = jwtUtil.extractUsername(token);
        Optional<User> userOptional = userRepo.findByUsername(username);

        if (userOptional.isEmpty()) {
            return new SignUpResponseDTO("User not found with username: " + username, HttpStatus.NOT_FOUND.value());
        }

        User user = userOptional.get();
        Long userId = user.getUserid();
        Cart cart = cartRepo.findByUserid(userId).orElse(null);

        if (cart == null) {
            cart = new Cart();
            cart.setUserid(userId);
            cartRepo.save(cart);  // Create a new cart for the user
        }

        for (PurchaseRequestDTO.ProductRequestDTO productRequestDTO : request.getProducts()) {
            Product product = productRepo.findById(productRequestDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Check if the product is already in the cart
            Optional<CartDetails> existingCartDetails = cartDetailsRepo.findByCartAndProduct(cart, product);
            CartDetails cartDetails;

            if (existingCartDetails.isPresent()) {
                // Product is already in the cart, update quantity and amount
                cartDetails = existingCartDetails.get();
                cartDetails.setQuantity(cartDetails.getQuantity() + productRequestDTO.getQuantity());
                cartDetails.setAmount(cartDetails.getAmount() + product.getPrice() * productRequestDTO.getQuantity());
            } else {
                // Product is not in the cart, add it as a new entry
                cartDetails = new CartDetails();
                cartDetails.setCart(cart);
                cartDetails.setProduct(product);
                cartDetails.setQuantity(productRequestDTO.getQuantity());
                cartDetails.setAmount(product.getPrice() * productRequestDTO.getQuantity());
            }

            // Save updated cart details
            cartDetailsRepo.save(cartDetails);

            // Reduce stock quantity for the product
            product.setStockQuantity(product.getStockQuantity() - productRequestDTO.getQuantity());
            productRepo.save(product);
        }

        return new SignUpResponseDTO("Products added to cart", HttpStatus.OK.value());
    }


    @Transactional
    public SignUpResponseDTO purchase(String token, PurchaseRequestDTO request) {
        String username = jwtUtil.extractUsername(token);
        Optional<User> userOptional = userRepo.findByUsername(username);

        if (userOptional.isEmpty()) {
            return new SignUpResponseDTO("User not found with username: " + username, HttpStatus.NOT_FOUND.value());
        }

        User user = userOptional.get();
        Long userId = user.getUserid();
        Cart cart = cartRepo.findByUserid(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        // Check if there are products in the cart
        List<CartDetails> cartDetailsList = cartDetailsRepo.findByCart(cart);
        if (cartDetailsList.isEmpty()) {
            return new SignUpResponseDTO("Cart is empty, nothing to purchase", HttpStatus.BAD_REQUEST.value());
        }

        for (PurchaseRequestDTO.ProductRequestDTO productRequest : request.getProducts()) {
            Product product = productRepo.findById(productRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            CartDetails cartDetails = cartDetailsRepo.findByCartAndProduct(cart, product)
                    .orElseThrow(() -> new RuntimeException("Product not found in cart"));

            // Handle transaction recording
            Transaction transaction = new Transaction();
            transaction.setCartDetails(cartDetails);
            transaction.setInvoiceNumber("INV-" + System.currentTimeMillis()); // Example invoice number generation
            transaction.setDate(LocalDateTime.now());
            transaction.setOrderDescription("Purchased " + product.getProductName());
            transaction.setQuantity(cartDetails.getQuantity());
            transaction.setAmount(cartDetails.getAmount());
            transactionRepo.save(transaction);

            // Update product stock
            product.setStockQuantity(product.getStockQuantity() - cartDetails.getQuantity());
            productRepo.save(product);
        }

        // Delete all cart details after purchase
        cartDetailsRepo.deleteAll(cartDetailsList);

        // Delete the cart after purchase
        cartRepo.delete(cart);

        return new SignUpResponseDTO("Purchase completed successfully and cart deleted", HttpStatus.OK.value());
    }

    @Transactional
    public SignUpResponseDTO increaseProductQuantity(String token, Long productId, int quantity) {
        String username = jwtUtil.extractUsername(token);
        Optional<User> userOptional = userRepo.findByUsername(username);

        if (userOptional.isEmpty()) {
            return new SignUpResponseDTO("User not found with username: " + username, HttpStatus.NOT_FOUND.value());
        }

        User user = userOptional.get();
        Long userId = user.getUserid();
        Cart cart = cartRepo.findByUserid(userId).orElseThrow(() -> new RuntimeException("Cart not found for user"));

        Product product = productRepo.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartDetails> existingCartDetails = cartDetailsRepo.findByCartAndProduct(cart, product);
        if (existingCartDetails.isEmpty()) {
            return new SignUpResponseDTO("Product not found in the cart", HttpStatus.NOT_FOUND.value());
        }

        CartDetails cartDetails = existingCartDetails.get();
        cartDetails.setQuantity(cartDetails.getQuantity() + quantity);
        cartDetails.setAmount(cartDetails.getAmount() + product.getPrice() * quantity);
        cartDetailsRepo.save(cartDetails);

        // Reduce stock quantity for the product
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepo.save(product);

        return new SignUpResponseDTO("Product quantity increased in cart", HttpStatus.OK.value());
    }


    @Transactional
    public SignUpResponseDTO decreaseProductQuantity(String token, Long productId, int quantity) {
        String username = jwtUtil.extractUsername(token);
        Optional<User> userOptional = userRepo.findByUsername(username);

        if (userOptional.isEmpty()) {
            return new SignUpResponseDTO("User not found with username: " + username, HttpStatus.NOT_FOUND.value());
        }

        User user = userOptional.get();
        Long userId = user.getUserid();
        Cart cart = cartRepo.findByUserid(userId).orElseThrow(() -> new RuntimeException("Cart not found for user"));

        Product product = productRepo.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartDetails> existingCartDetails = cartDetailsRepo.findByCartAndProduct(cart, product);
        if (existingCartDetails.isEmpty()) {
            return new SignUpResponseDTO("Product not found in the cart", HttpStatus.NOT_FOUND.value());
        }

        CartDetails cartDetails = existingCartDetails.get();
        int updatedQuantity = cartDetails.getQuantity() - quantity;

        if (updatedQuantity < 0) {
            return new SignUpResponseDTO("Cannot decrease quantity below zero", HttpStatus.BAD_REQUEST.value());
        } else if (updatedQuantity == 0) {
            cartDetails.setQuantity(0);
            cartDetails.setAmount(0.0);  // Reset amount to zero
        } else {
            cartDetails.setQuantity(updatedQuantity);
            cartDetails.setAmount(updatedQuantity * product.getPrice());
        }

        cartDetailsRepo.save(cartDetails);

        // Increase stock quantity for the product
        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepo.save(product);

        return new SignUpResponseDTO("Product quantity decreased in cart", HttpStatus.OK.value());
    }
}
