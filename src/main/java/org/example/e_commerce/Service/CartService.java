//package org.example.e_commerce.Service;
//
//import jakarta.transaction.Transactional;
//import org.example.e_commerce.Entity.*;
//import org.example.e_commerce.Repository.*;
//import org.example.e_commerce.dto.dtoRequest.PurchaseRequestDTO;
//import org.example.e_commerce.dto.dtoResponse.SignUpResponseDTO;
//import org.example.e_commerce.util.JwtUtil;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@Service
//public class CartService {
//
//    @Autowired
//    ProductRepository productRepo;
//
//    @Autowired
//    private ModelMapper modelMapper;
//
//    @Autowired
//    CartRepo cartRepo;
//
//    @Autowired
//    CartDetailsRepo cartDetailsRepo;
//
//    @Autowired
//    TransactionRepo transactionRepo;
//
//    @Autowired
//    UserRepository userrepo;
//
//    @Autowired
//    JwtUtil jwtUtil;
//
//    SignUpResponseDTO responseDTO = new SignUpResponseDTO();
//
//    @Transactional
//    public SignUpResponseDTO AddToCart(String token, PurchaseRequestDTO.ProductRequestDTO productRequestDTO) {
//        String username = jwtUtil.extractUsername(token);
//        Optional<User> userOptional = userrepo.findByUsername(username);
//
//        if (!userOptional.isPresent()) {
//            responseDTO.setMessage("User not found with username: " + username);
//            responseDTO.setStatusCode(-1);
//            return responseDTO;
//        }
//
//        User user = userOptional.get();
//        Long userId = user.getUserid();
//        Cart cart = cartRepo.findByUserid(userId).orElse(null);
//
//        if (cart == null) {
//            cart = new Cart();
//            cart.setUserid(userId);
//            cartRepo.save(cart);
//            responseDTO.setMessage("CART CREATED");
//            responseDTO.setStatusCode(0);
//            return responseDTO;
//        }
//
//        Product product = productRepo.findById(productRequestDTO.getProductId())
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//
//        if (product.getStockQuantity() < productRequestDTO.getQuantity()) {
//            responseDTO.setMessage("Insufficient stock for product");
//            responseDTO.setStatusCode(-1);
//            return responseDTO;
//        }
//
//        CartDetails cartDetails = new CartDetails();
//        cartDetails.setCart(cart);
//        cartDetails.setProduct(product);
//        cartDetails.setQuantity(productRequestDTO.getQuantity());
//        cartDetails.setAmount(product.getPrice() * productRequestDTO.getQuantity());
//        cartDetailsRepo.save(cartDetails);
//
//        responseDTO.setMessage("Products Added To Cart");
//        responseDTO.setStatusCode(0);
//        return responseDTO;
//    }
//
//    @Transactional
//    public SignUpResponseDTO purchase(PurchaseRequestDTO request) {
//        try {
//            Cart cart = cartRepo.findByUserid(request.getUserId())
//                    .orElseThrow(() -> new RuntimeException("Cart not found for user"));
//
//            double totalAmount = 0.0;
//            for (PurchaseRequestDTO.ProductRequestDTO productRequest : request.getProducts()) {
//                Optional<Product> optionalProduct = productRepo.findById(productRequest.getProductId());
//                if (!optionalProduct.isPresent()) {
//                    continue;
//                }
//                Product product = optionalProduct.get();
//
//                if (product.getStockQuantity() < productRequest.getQuantity()) {
//                    return new SignUpResponseDTO("Insufficient stock for product: " + productRequest.getProductId(), HttpStatus.BAD_REQUEST.value());
//                }
//
//                double amount = product.getPrice() * productRequest.getQuantity();
//                totalAmount += amount;
//
//                CartDetails cartDetails = new CartDetails();
//                cartDetails.setCart(cart);
//                cartDetails.setProduct(product);
//                cartDetails.setQuantity(productRequest.getQuantity());
//                cartDetails.setAmount(amount);
//                cartDetailsRepo.save(cartDetails);
//
//                product.setStockQuantity(product.getStockQuantity() - productRequest.getQuantity());
//                productRepo.save(product);
//            }
//
//            String invoiceNumber = "INV-" + System.currentTimeMillis();
//            LocalDateTime date = LocalDateTime.now();
//
//            Transaction transaction = new Transaction();
//            transaction.setCartDetails(cartDetailsRepo.findByCart(cart));
//            transaction.setInvoiceNumber(invoiceNumber);
//            transaction.setDate(date);
//            transaction.setOrderDescription("Purchase for user " + request.getUserId());
//            transaction.setQuantity(request.getProducts().stream().mapToInt(PurchaseRequestDTO.ProductRequestDTO::getQuantity).sum());
//            transaction.setAmount(totalAmount);
//            transactionRepo.save(transaction);
//
//            return new SignUpResponseDTO("Purchase successful. Invoice Number: " + invoiceNumber, HttpStatus.OK.value());
//        } catch (RuntimeException e) {
//            return new SignUpResponseDTO("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
//        }
//    }
//}
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
    public SignUpResponseDTO purchase(PurchaseRequestDTO request) {
        try {
            // Retrieve the user's cart
            Cart cart = cartRepo.findByUserid(request.getUserId())
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
                transaction.setOrderDescription("Purchase for user " + request.getUserId());
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
}
