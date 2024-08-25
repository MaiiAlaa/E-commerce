package org.example.e_commerce.dto.dtoResponse;

import lombok.Data;
import java.util.List;

@Data
public class ProductsResponseDTO {
    private Long statusCode;
    private String message;
    private List<ProductDTO> products;

    public ProductsResponseDTO(Long statusCode, String message, List<ProductDTO> products) {
        this.statusCode = statusCode;
        this.message = message;
        this.products = products;
    }

    public ProductsResponseDTO() {
    }
}
