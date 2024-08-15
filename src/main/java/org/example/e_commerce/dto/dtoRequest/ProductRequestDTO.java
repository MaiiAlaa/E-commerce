package org.example.e_commerce.dto.dtoRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.example.e_commerce.Entity.Product;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductRequestDTO {

    List<Product> productList = new ArrayList<>();

    private Long productID;

    @NotBlank(message = "Product name is mandatory")
    @NotNull(message = "Product name is mandatory")
    private String productName;

    @NotNull(message = "Category ID mandatory")
    @NotBlank(message = "Category ID mandatory")
    private Long categoryID;

    @NotNull(message = "Price is mandatory")
    @Positive(message = "Price must be positive")
    @NotBlank(message = "Price is mandatory")
    private Double price;

    @NotNull(message = "Stock quantity is mandatory")
    @Positive(message = "Stock quantity must be positive")
    @NotBlank(message = "Stock quantity is mandatory")
    private Integer stockQuantity;

    @NotBlank(message = "Description is mandatory")
    @NotNull(message = "Description is mandatory")
    private String description;

    @NotNull(message = "Warranty period is mandatory")
    @Positive(message = "Warranty period must be positive")
    @NotBlank(message = "Warranty period is mandatory")
    private Integer warrantyPeriod;

    @NotBlank(message = "Manufacturer is mandatory")
    @NotNull(message = "Manufacturer is mandatory")
    private String manufacturer;

    @NotBlank(message = "Main image URL is mandatory")
    @NotNull(message = "Main image URL is mandatory")
    private String mainImageUrl;

    private List<String> imageUrls = new ArrayList<>();

}
