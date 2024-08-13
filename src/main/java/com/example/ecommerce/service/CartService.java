package com.example.ecommerce.service;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.dto.response.SignUpResponseDTO;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    ProductRepo productRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    CartRepo cartRepo;
    @Autowired
    CartDetailsRepo cartDetailsRepo;
    @Autowired
    TransactionRepo transactionRepo;
    @Autowired
    Userrepo userrepo;
    @Autowired
    JwtUtil jwtUtil;
    SignUpResponseDTO responseDTO = new SignUpResponseDTO();
    @Transactional
    public SignUpResponseDTO AddToCart(String Token ,PurchaseRequestDTO.ProductRequestDTO productRequestDTO)
    {
        String username = jwtUtil.extractUsername(Token);
        User user = new User();
        Long userId =userrepo.getUserIdByUsername(username);
        Cart cart = cartRepo.findByUserId(userId).orElse(null);
        if (cart == null)
        {
            cart = new Cart();
            cart.setUser(user);
            cartRepo.save(cart);
            responseDTO.setMessage("CART CREATED");
            responseDTO.setStatusCode(0l);
            return responseDTO;
        }
        Product product = productRepo.findById(productRequestDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStockQuantity() < productRequestDTO.getQuantity()) {
            responseDTO.setMessage("Insufficient stock for product");
            responseDTO.setStatusCode(-1l);
            return responseDTO;
        }
        CartDetails cartDetails = new CartDetails();
        cartDetails.setCart(cart);
        cartDetails.setProduct(product);
        cartDetails.setQuantity(productRequestDTO.getQuantity());
        cartDetails.setAmount(product.getPrice()*productRequestDTO.getQuantity());
        cartDetailsRepo.save(cartDetails);
        responseDTO.setMessage("Products Added To Cart");
        responseDTO.setStatusCode(0l);
        return responseDTO;
    }
    @Transactional
    public SignUpResponseDTO purchase(PurchaseRequestDTO request) {
        try {
            Cart cart = cartRepo.findByUserId(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Cart not found for user"));

            double totalAmount = 0.0;
            for (PurchaseRequestDTO.ProductRequestDTO productRequest : request.getProducts()) {
                Optional<Product> optionalProduct = productRepo.findById(productRequest.getProductId());
                if (!optionalProduct.isPresent()) {
                    continue;
                }
                Product product = optionalProduct.get();

                if (product.getStockQuantity() < productRequest.getQuantity()) {
                    return new SignUpResponseDTO("Insufficient stock for product: " + productRequest.getProductId(), (long) HttpStatus.BAD_REQUEST.value());
                    //double check for the product 3shan lw khles f nfs lwaat w had tane bytlob
                }
                double amount = product.getPrice() * productRequest.getQuantity();
                totalAmount += amount;
                CartDetails cartDetails = new CartDetails();
                cartDetails.setCart(cart);
                cartDetails.setProduct(product);
                cartDetails.setQuantity(productRequest.getQuantity());
                cartDetails.setAmount(amount);
                cartDetailsRepo.save(cartDetails);

                product.setStockQuantity(product.getStockQuantity() - productRequest.getQuantity());
                productRepo.save(product);
            }
            String invoiceNumber = "INV-" + System.currentTimeMillis();
            LocalDateTime date = LocalDateTime.now();

            Transaction transaction = new Transaction();
            transaction.setCartDetails(cartDetailsRepo.findByCart(cart));
            transaction.setInvoiceNumber(invoiceNumber);
            transaction.setDate(date);
            transaction.setOrderDescription("Purchase for user " + request.getUserId());
            transaction.setQuantity(request.getProducts().stream().mapToInt(PurchaseRequestDTO.ProductRequestDTO::getQuantity).sum());
            transaction.setAmount(totalAmount);
            transactionRepo.save(transaction);
            return new SignUpResponseDTO("Purchase successful. Invoice Number: " + invoiceNumber, (long) HttpStatus.OK.value());
        } catch (RuntimeException e) {
            return new SignUpResponseDTO("Error: " + e.getMessage(), (long) HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}