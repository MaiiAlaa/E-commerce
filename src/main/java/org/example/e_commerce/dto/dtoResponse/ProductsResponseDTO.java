package org.example.e_commerce.dto.dtoResponse;

import lombok.Data;
import java.util.List;

@Data
public class ProductsResponseDTO {
    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private Long category_id;
    private String category_name;
    private String mainImageUrl; // Main image URL
    private String message; // Validation or success message
    private Long statusCode;

    public ProductsResponseDTO(Long id, String name, Double price, Integer stock, Long category_id, String category_name , String mainImageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category_id = category_id;
        this.category_name = category_name;
        this.mainImageUrl = mainImageUrl;
    }
    public ProductsResponseDTO(String message, Long statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public ProductsResponseDTO() {
    }
}