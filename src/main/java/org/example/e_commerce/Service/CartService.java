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
                    Product product = cartDetails.getProduct();
                    int quantity = cartDetails.getQuantity();
                    double itemTotalPrice = product.getPrice() * quantity; // Calculate item total price

                    totalCartPrice[0] += itemTotalPrice; // Add to total cart price

                    return new cartProductDetailsDTO(
                            product.getProductId(),
                            product.getProductName(),
                            product.getImageUrl(), // Assuming imageUrl holds the main image
                            product.getPrice(),
                            quantity,
                            itemTotalPrice
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
        try {
            // Extract the username from the token
            String username = jwtUtil.extractUsername(token);
            Optional<User> userOptional = userRepo.findByUsername(username);

            if (userOptional.isEmpty()) {
                return new SignUpResponseDTO("User not found with username: " + username, HttpStatus.NOT_FOUND.value());
            }

            User user = userOptional.get();
            Long userId = user.getUserid();

            // Retrieve the user's cart
            Cart cart = cartRepo.findByUserid(userId)
                    .orElseThrow(() -> new RuntimeException("Cart not found for user"));

            double totalAmount = 0.0;
            String invoiceNumber = "INV-" + System.currentTimeMillis();

            for (PurchaseRequestDTO.ProductRequestDTO productRequest : request.getProducts()) {
                Product product = productRepo.findById(productRequest.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                // Check if the product exists in the cart
                CartDetails cartDetails = cartDetailsRepo.findByCartAndProduct(cart, product)
                        .orElseThrow(() -> new RuntimeException("Product not found in cart"));

                // Check if the requested quantity is greater than the quantity in the cart
                if (productRequest.getQuantity() > cartDetails.getQuantity()) {
                    return new SignUpResponseDTO("Requested quantity exceeds the quantity in the cart for product: "
                            + productRequest.getProductId(), HttpStatus.BAD_REQUEST.value());
                }

                // Check if there's sufficient stock for the purchase
                if (product.getStockQuantity() < productRequest.getQuantity()) {
                    return new SignUpResponseDTO("Insufficient stock for product: "
                            + productRequest.getProductId(), HttpStatus.BAD_REQUEST.value());
                }

                double amount = product.getPrice() * productRequest.getQuantity();
                totalAmount += amount;

                // Update the quantity in CartDetails
                int updatedQuantity = cartDetails.getQuantity() - productRequest.getQuantity();

                if (updatedQuantity == 0) {
                    // Set quantity to zero, but don't delete
                    cartDetails.setQuantity(0);
                    cartDetails.setAmount(0.0);  // Reset amount to zero
                } else {
                    cartDetails.setQuantity(updatedQuantity);
                    cartDetails.setAmount(updatedQuantity * product.getPrice());
                }
                cartDetailsRepo.save(cartDetails);

                // Update product stock
                product.setStockQuantity(product.getStockQuantity() - productRequest.getQuantity());
                productRepo.save(product);

                // Create a transaction record
                Transaction transaction = new Transaction();
                transaction.setCartDetails(cartDetails);  // Keep the link to CartDetails
                transaction.setInvoiceNumber("INV-" + System.currentTimeMillis());
                transaction.setDate(LocalDateTime.now());
                transaction.setOrderDescription("Purchase for user " + userId);
                transaction.setQuantity(productRequest.getQuantity());  // Set purchased quantity
                transaction.setAmount(amount);  // Set the correct amount
                transactionRepo.save(transaction);
            }

            // Return response with invoice number in the message
            return new SignUpResponseDTO("Purchase successful. Invoice Number: " + invoiceNumber, HttpStatus.OK.value());
        } catch (RuntimeException e) {
            return new SignUpResponseDTO("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
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
