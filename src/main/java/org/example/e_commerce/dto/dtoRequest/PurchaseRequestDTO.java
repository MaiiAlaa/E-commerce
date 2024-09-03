package org.example.e_commerce.dto.dtoRequest;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseRequestDTO {

    private Long userId;  // User ID for the purchase
    private List<ProductRequestDTO> products;  // List of products to be added

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<ProductRequestDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductRequestDTO> products) {
        this.products = products;
    }

    // Inner class to represent individual product requests
    @Data
    public static class ProductRequestDTO {
        private Long productId;  // ID of the product to be added
        private int quantity;    // Quantity of the product to be added

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
