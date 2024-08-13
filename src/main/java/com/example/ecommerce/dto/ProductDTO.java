package com.example.ecommerce.dto;

import com.example.ecommerce.entity.Product;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductDTO {
        // vilidation hena mydakhlsh haga b null
        List<Product> productList = new ArrayList<>();
    private Long productID;
    private String productName;
    private Long categoryID; // Reference to Category table
    private Double price;
    private Integer stockQuantity;
    private String description;
    private Integer warrantyPeriod;
    private String manufacturer;
}
