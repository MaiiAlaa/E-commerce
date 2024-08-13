package com.example.ecommerce.dto;
import lombok.Data;

@Data
public class CartDetailsDTO {
    private Long cartDetailsId;
    private Long cartId;
    private Long productId;
    private int quantity;
    private double amount;
}
