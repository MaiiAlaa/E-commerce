package org.example.e_commerce.dto.dtoRequest;

import lombok.Data;
import org.example.e_commerce.Entity.Product;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductRequestDTO {
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
