package org.example.e_commerce.dto.dtoRequest;

import java.time.LocalDateTime;

public class FavoriteRequestDTO {

    private Long favoriteId;
    private Long userId;
    private Long productId;
    private Long categoryId;
    private LocalDateTime createdAt;

    // Constructors
    public FavoriteRequestDTO() {
    }

    public FavoriteRequestDTO(Long favoriteId, Long userId, Long productId, Long categoryId, LocalDateTime createdAt) {
        this.favoriteId = favoriteId;
        this.userId = userId;
        this.productId = productId;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
