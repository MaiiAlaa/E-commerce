package org.example.e_commerce.dto.dtoResponse;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductResponseDTO {
    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private String description;
    private Long category_id;
    private String category_name;

    public ProductResponseDTO(Long id, String name, Double price, Integer stock, String description, Long category_id, String category_name) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.category_id = category_id;
        this.category_name = category_name;
    }
}