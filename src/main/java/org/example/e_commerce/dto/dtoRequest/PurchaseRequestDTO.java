package org.example.e_commerce.dto.dtoRequest;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseRequestDTO {

    private Long userId;
    private List<ProductRequestDTO> products;

    public static class ProductRequestDTO {

        private Long productId;
        private int quantity;

        // Getters and Setters
        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}