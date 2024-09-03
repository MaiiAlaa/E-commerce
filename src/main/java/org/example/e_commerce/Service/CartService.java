package org.example.e_commerce.Service;

import jakarta.transaction.Transactional;
import org.example.e_commerce.Entity.*;
import org.example.e_commerce.Repository.*;
import org.example.e_commerce.dto.dtoRequest.PurchaseRequestDTO;
import org.example.e_commerce.dto.dtoResponse.SignUpResponseDTO;
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

    // Add multiple products to the cart
    @Transactional
    public SignUpResponseDTO addToCart(String token, PurchaseRequestDTO request) {
        String username = jwtUtil.extractUsername(token);  // Extract username from JWT token
        Optional<User> userOptional = userRepo.findByUsername(username);  // Fetch user details

        if (userOptional.isEmpty()) {
            return new SignUpResponseDTO("User not found with username: " + username, HttpStatus.NOT_FOUND.value());
        }

        User user = userOptional.get();
        Long userId = user.getUserid();
        Cart cart = cartRepo.findByUserid(userId).orElse(null);  // Find or create the user's cart

        if (cart == null) {
            cart = new Cart();
            cart.setUserid(userId);
            cartRepo.save(cart);  // Save new cart if not present
        }

        // Loop through each product to be added to the cart
        for (PurchaseRequestDTO.ProductRequestDTO productRequestDTO : request.getProducts()) {
            Product product = productRepo.findById(productRequestDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStockQuantity() < productRequestDTO.getQuantity()) {
                return new SignUpResponseDTO("Insufficient stock for product: " + product.getProductId(), HttpStatus.BAD_REQUEST.value());
            }

            Optional<CartDetails> existingCartDetails = cartDetailsRepo.findByCartAndProduct(cart, product);
            CartDetails cartDetails;

            if (existingCartDetails.isPresent()) {
                cartDetails = existingCartDetails.get();
                cartDetails.setQuantity(cartDetails.getQuantity() + productRequestDTO.getQuantity());
                cartDetails.setAmount(cartDetails.getAmount() + product.getPrice() * productRequestDTO.getQuantity());
            } else {
                cartDetails = new CartDetails();
                cartDetails.setCart(cart);
                cartDetails.setProduct(product);
                cartDetails.setQuantity(productRequestDTO.getQuantity());
                cartDetails.setAmount(product.getPrice() * productRequestDTO.getQuantity());
            }

            cartDetailsRepo.save(cartDetails);  // Save the updated or new cart details

            // Update the product stock
            product.setStockQuantity(product.getStockQuantity() - productRequestDTO.getQuantity());
            productRepo.save(product);
        }

        return new SignUpResponseDTO("Products Added To Cart", HttpStatus.OK.value());
    }

    // Purchase the products in the user's cart
    @Transactional
    public SignUpResponseDTO purchase(PurchaseRequestDTO request) {
        try {
            Cart cart = cartRepo.findByUserid(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Cart not found for user"));

            double totalAmount = 0.0;
            for (PurchaseRequestDTO.ProductRequestDTO productRequest : request.getProducts()) {
                Product product = productRepo.findById(productRequest.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                if (product.getStockQuantity() < productRequest.getQuantity()) {
                    return new SignUpResponseDTO("Insufficient stock for product: " + productRequest.getProductId(), HttpStatus.BAD_REQUEST.value());
                }

                double amount = product.getPrice() * productRequest.getQuantity();
                totalAmount += amount;

                Optional<CartDetails> existingCartDetails = cartDetailsRepo.findByCartAndProduct(cart, product);
                CartDetails cartDetails;

                if (existingCartDetails.isPresent()) {
                    cartDetails = existingCartDetails.get();
                    cartDetails.setQuantity(cartDetails.getQuantity() + productRequest.getQuantity());
                    cartDetails.setAmount(cartDetails.getAmount() + amount);
                } else {
                    cartDetails = new CartDetails();
                    cartDetails.setCart(cart);
                    cartDetails.setProduct(product);
                    cartDetails.setQuantity(productRequest.getQuantity());
                    cartDetails.setAmount(amount);
                }

                cartDetailsRepo.save(cartDetails);

                product.setStockQuantity(product.getStockQuantity() - productRequest.getQuantity());
                productRepo.save(product);
            }

            String invoiceNumber = "INV-" + System.currentTimeMillis();
            LocalDateTime date = LocalDateTime.now();

            List<CartDetails> cartDetailsList = cartDetailsRepo.findByCart(cart);
            for (CartDetails cartDetails : cartDetailsList) {
                Transaction transaction = new Transaction();
                transaction.setCartDetails(cartDetails);
                transaction.setInvoiceNumber(invoiceNumber);
                transaction.setDate(date);
                transaction.setOrderDescription("Purchase for user " + request.getUserId());
                transaction.setQuantity(cartDetails.getQuantity());
                transaction.setAmount(cartDetails.getAmount());
                transactionRepo.save(transaction);
            }

            return new SignUpResponseDTO("Purchase successful. Invoice Number: " + invoiceNumber, HttpStatus.OK.value());
        } catch (RuntimeException e) {
            return new SignUpResponseDTO("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
