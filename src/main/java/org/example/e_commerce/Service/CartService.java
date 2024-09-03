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

    @Transactional
    public SignUpResponseDTO addToCart(String token, PurchaseRequestDTO request) {
        if (request == null || request.getProducts() == null || request.getProducts().isEmpty()) {
            return new SignUpResponseDTO("Invalid request: Products list is empty", HttpStatus.BAD_REQUEST.value());
        }

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
            cartRepo.save(cart);
        }

        for (PurchaseRequestDTO.ProductRequestDTO productRequestDTO : request.getProducts()) {
            Product product = productRepo.findById(productRequestDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStockQuantity() < productRequestDTO.getQuantity()) {
                return new SignUpResponseDTO("Insufficient stock for product: " + product.getProductName(), HttpStatus.BAD_REQUEST.value());
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

            cartDetailsRepo.save(cartDetails);
            product.setStockQuantity(product.getStockQuantity() - productRequestDTO.getQuantity());
            productRepo.save(product);
        }

        return new SignUpResponseDTO("Products added to cart successfully", HttpStatus.OK.value());
    }

    @Transactional
    public SignUpResponseDTO purchase(String token, PurchaseRequestDTO request) {
        if (request == null || request.getProducts() == null || request.getProducts().isEmpty()) {
            return new SignUpResponseDTO("Invalid request: Products list is empty", HttpStatus.BAD_REQUEST.value());
        }

        String username = jwtUtil.extractUsername(token);
        Optional<User> userOptional = userRepo.findByUsername(username);

        if (userOptional.isEmpty()) {
            return new SignUpResponseDTO("User not found with username: " + username, HttpStatus.NOT_FOUND.value());
        }

        User user = userOptional.get();
        Long userId = user.getUserid();

        Cart cart = cartRepo.findByUserid(userId)
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
            transaction.setOrderDescription("Purchase for user " + userId);
            transaction.setQuantity(cartDetails.getQuantity());
            transaction.setAmount(cartDetails.getAmount());
            transactionRepo.save(transaction);
        }

        return new SignUpResponseDTO("Purchase successful. Invoice Number: " + invoiceNumber, HttpStatus.OK.value());
    }
}
