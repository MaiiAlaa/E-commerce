package org.example.e_commerce.dto.dtoRequest;

import java.time.LocalDateTime;

public class FavoriteRequestDTO {

    private Long favoriteId;
    private Long userId;
    private Long productId;
    private Long categoryId;

    // Constructors
    public FavoriteRequestDTO() {
    }

    public FavoriteRequestDTO(Long favoriteId, Long userId, Long productId, Long categoryId) {
        this.favoriteId = favoriteId;
        this.userId = userId;
        this.productId = productId;
        this.categoryId = categoryId;
    }

    // Getters and Setters

    public Long getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(Long favoriteId) {
        this.favoriteId = favoriteId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
