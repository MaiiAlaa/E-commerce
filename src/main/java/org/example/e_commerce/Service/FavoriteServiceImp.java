package org.example.e_commerce.Service;

import org.example.e_commerce.dto.dtoRequest.FavoriteRequestDTO;
import org.example.e_commerce.Entity.Favorite;
import org.example.e_commerce.Entity.User;
import org.example.e_commerce.Entity.Product;
import org.example.e_commerce.Entity.Category;
import org.example.e_commerce.Repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImp implements FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Override
    public FavoriteRequestDTO saveFavorite(FavoriteRequestDTO favoriteRequestDTO, User user, Product product, Category category) {
        Favorite favorite = convertToEntity(favoriteRequestDTO, user, product, category);
        Favorite savedFavorite = favoriteRepository.save(favorite);
        return convertToDto(savedFavorite);
    }

    @Override
    public List<FavoriteRequestDTO> getAllFavoritesByUserId(Long userId) {
        List<Favorite> favorites = favoriteRepository.findAllByUser_Userid(userId);
        return favorites.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<FavoriteRequestDTO> getFavoriteById(Long favoriteId) {
        Optional<Favorite> favorite = favoriteRepository.findById(favoriteId);
        return favorite.map(this::convertToDto);
    }

    @Override
    public void deleteFavorite(Long favoriteId) {
        favoriteRepository.deleteById(favoriteId);
    }

    private FavoriteRequestDTO convertToDto(Favorite favorite) {
        return new FavoriteRequestDTO(
                favorite.getFavoriteId(),
                favorite.getUser().getUserid(),
                favorite.getProduct() != null ? favorite.getProduct().getProductid() : null,
                favorite.getCategory() != null ? favorite.getCategory().getCategoryid() : null,
                favorite.getCreatedAt()
        );
    }

    private Favorite convertToEntity(FavoriteRequestDTO favoriteRequestDTO, User user, Product product, Category category) {
        Favorite favorite = new Favorite();
        favorite.setFavoriteId(favoriteRequestDTO.getFavoriteId());
        favorite.setUser(user);
        favorite.setProduct(product);
        favorite.setCategory(category);
        favorite.setCreatedAt(favoriteRequestDTO.getCreatedAt());
        return favorite;
    }
}
