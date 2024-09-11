package org.example.e_commerce.dto.dtoResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class cartProductDetailsDTO {
        private Long productId;
        private String productName;
        private String productMainImage;
        private double productPrice;
        private int quantity;
        private double itemTotalPrice;
        private int cartSize; // price * quantity

        public cartProductDetailsDTO(Long productId, String productName, String imageUrl, Double price, int quantity, double itemTotalPrice) {
        }

        public cartProductDetailsDTO(Long productId, String productName, String imageUrl, Double price, int finalCartSize) {
        }
}
