package org.example.e_commerce.dto.dtoRequest;

import lombok.Data;

@Data
public class CartDetailsDTO {
    private Long cartDetailsId;
    private Long cartId;
    private Long productId;
    private int quantity;
    private double amount;
}
