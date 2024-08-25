package org.example.e_commerce.dto.dtoResponse;

import lombok.Data;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private Long categoryId;
    private String categoryName;
    private String mainImageUrl;

    public ProductDTO(Long id, String name, Double price, Integer stock, Long categoryId, String categoryName, String mainImageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.mainImageUrl = mainImageUrl;
    }

    public ProductDTO() {
    }
}