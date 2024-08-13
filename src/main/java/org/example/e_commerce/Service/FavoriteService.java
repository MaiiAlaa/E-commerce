package org.example.e_commerce.Service;

import org.example.e_commerce.dto.dtoRequest.FavoriteRequestDTO;
import org.example.e_commerce.Entity.User;
import org.example.e_commerce.Entity.Product;
import org.example.e_commerce.Entity.Category;

import java.util.List;
import java.util.Optional;

public interface FavoriteService {

    FavoriteRequestDTO saveFavorite(FavoriteRequestDTO favoriteDTO, User user, Product product, Category category);

    List<FavoriteRequestDTO> getAllFavoritesByUserId(Long userId);

    Optional<FavoriteRequestDTO> getFavoriteById(Long favoriteId);

    void deleteFavorite(Long favoriteId);
}
