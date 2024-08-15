package org.example.e_commerce.dto.dtoRequest;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductRequestDTO {

    @NotNull(message = "Product ID is mandatory")
    private Long productID;

    @NotBlank(message = "Product name is mandatory")
    private String productName;

    @NotNull(message = "Category ID mandatory")
    private Long categoryID;

    @NotNull(message = "Price is mandatory")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Stock quantity is mandatory")
    @Positive(message = "Stock quantity must be positive")
    private Integer stockQuantity;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull(message = "Warranty period is mandatory")
    @Positive(message = "Warranty period must be positive")
    private Integer warrantyPeriod;

    @NotBlank(message = "Manufacturer is mandatory")
    private String manufacturer;

    @NotBlank(message = "Main image URL is mandatory")
    private String mainImageUrl;

    private List<String> imageUrls = new ArrayList<>();
}
