package org.example.e_commerce.dto.dtoResponse;

public class ProductResponseDTO {

    private Long productId;
    private String productName;
    private Double price;
    private Integer stockQuantity;
    private String description;
    private Long categoryId;
    private String categoryName;

    // Constructor
    public ProductResponseDTO(Long productId, String productName, Double price, Integer stockQuantity,
                              String description, Long categoryId, String categoryName) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    // Getters and Setters (if needed)
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}