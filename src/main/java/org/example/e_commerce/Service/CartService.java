package org.example.e_commerce.Service;

import jakarta.transaction.Transactional;
import org.example.e_commerce.Entity.*;
import org.example.e_commerce.Repository.*;
import org.example.e_commerce.dto.dtoRequest.PurchaseRequestDTO;
import org.example.e_commerce.dto.dtoResponse.CartResponseDTO;
import org.example.e_commerce.dto.dtoResponse.PurchaseResponseDTO;
import org.example.e_commerce.dto.dtoResponse.SignUpResponseDTO;
import org.example.e_commerce.dto.dtoResponse.cartProductDetailsDTO;
import org.example.e_commerce.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        List<CartDetails> cartDetailsList = cartDetailsRepo.findByCartAndIsPurchasedFalse(cart);

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
    public PurchaseResponseDTO purchase(String token, PurchaseRequestDTO request) {
        PurchaseResponseDTO response = new PurchaseResponseDTO();

        try {
            // Extract the username from the token
            String username = jwtUtil.extractUsername(token);
            Optional<User> userOptional = userRepo.findByUsername(username);

            if (userOptional.isEmpty()) {
                response.setMessage("User not found with username: " + username);
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                return response;
            }

            User user = userOptional.get();
            Long userId = user.getUserid();

            double totalAmount = 0.0;
            String invoiceNumber = "INV-" + System.currentTimeMillis(); // Generate a unique invoice number

            for (PurchaseRequestDTO.ProductRequestDTO productRequest : request.getProducts()) {
                Product product = productRepo.findById(productRequest.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                // Check if the product exists in the cart
                Cart cart = cartRepo.findByUserid(userId)
                        .orElseThrow(() -> new RuntimeException("Cart not found for user"));

                CartDetails cartDetails = cartDetailsRepo.findByCartAndProduct(cart, product)
                        .orElseThrow(() -> new RuntimeException("Product not found in cart"));

                // Check if the requested quantity is greater than the quantity in the cart
                if (productRequest.getQuantity() > cartDetails.getQuantity()) {
                    response.setMessage("Requested quantity exceeds the quantity in the cart for product: "
                            + productRequest.getProductId());
                    response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                    return response;
                }

                // Check if there's sufficient stock for the purchase
                if (product.getStockQuantity() < productRequest.getQuantity()) {
                    response.setMessage("Insufficient stock for product: "
                            + productRequest.getProductId());
                    response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                    return response;
                }

                double amount = product.getPrice() * productRequest.getQuantity();
                totalAmount += amount;

                int newCartQuantity = cartDetails.getQuantity() - productRequest.getQuantity();
                cartDetails.setQuantity(newCartQuantity);
                cartDetailsRepo.save(cartDetails);

                // If the quantity in the cart becomes zero, do not remove it (Solution 1)
                if (newCartQuantity == 0) {
                    // You could add a status or flag to the cart details to indicate it's empty if needed
                    cartDetails.setQuantity(0);
                    cartDetails.setPurchased(true);
                    cartDetailsRepo.save(cartDetails); // Still save the updated cart details
                }

                // Update product stock
                product.setStockQuantity(product.getStockQuantity() - productRequest.getQuantity());
                productRepo.save(product);

                // Create a transaction record
                Transaction transaction = new Transaction();
                transaction.setCartDetails(cartDetails);
                transaction.setInvoiceNumber(invoiceNumber);
                transaction.setDate(LocalDateTime.now());
                transaction.setOrderDescription("Purchase for user " + userId);
                transaction.setQuantity(productRequest.getQuantity());
                transaction.setAmount(amount);
                transactionRepo.save(transaction);
            }

            // Return response with invoice number included separately
            response.setMessage("Purchase successful.");
            response.setInvoiceNumber(invoiceNumber);
            response.setStatusCode(HttpStatus.OK.value());
        } catch (RuntimeException e) {
            response.setMessage("Error: " + e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
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
