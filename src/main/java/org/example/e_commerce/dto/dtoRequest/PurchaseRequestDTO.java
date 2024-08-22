package org.example.e_commerce.dto.dtoRequest;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseRequestDTO {
    private Long userId;
    private List<ProductRequestDTO> products;

    @Data
    public static class ProductRequestDTO {
        private Long productId;
        private int quantity;
    }
}
