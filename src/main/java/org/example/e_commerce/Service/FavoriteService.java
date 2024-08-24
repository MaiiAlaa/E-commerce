package org.example.e_commerce.Service;

import org.example.e_commerce.Entity.Category;
import org.example.e_commerce.Entity.Favorite;
import org.example.e_commerce.Entity.Product;
import org.example.e_commerce.Entity.User;
import org.example.e_commerce.Repository.CategoryRepository;
import org.example.e_commerce.Repository.FavoriteRepository;
import org.example.e_commerce.Repository.ProductRepository;
import org.example.e_commerce.Repository.UserRepository;
import org.example.e_commerce.dto.dtoRequest.FavoriteRequestDTO;
import org.example.e_commerce.dto.dtoResponse.FavoriteResponseDTO;
import org.example.e_commerce.dto.dtoResponse.SignUpResponseDTO;
import org.example.e_commerce.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private static final Logger logger = Logger.getLogger(FavoriteService.class.getName());

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepository, UserRepository userRepository,
                           ProductRepository productRepository, CategoryRepository categoryRepository, JwtUtil jwtUtil) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.jwtUtil = jwtUtil;
    }

    public Favorite convertToEntity(FavoriteRequestDTO favoriteRequestDTO, User user, Product product, Category category) {
        Favorite favorite = modelMapper.map(favoriteRequestDTO, Favorite.class);
        favorite.setUser(user);
        favorite.setProduct(product);
        favorite.setCategory(category);
        return favorite;
    }

    public FavoriteResponseDTO convertToResponseDTO(Favorite favorite) {
        return modelMapper.map(favorite, FavoriteResponseDTO.class);
    }

    public SignUpResponseDTO addFavorite(String token, FavoriteRequestDTO favoriteRequestDTO) {
        SignUpResponseDTO signUpResponseDTO = new SignUpResponseDTO();
        Long userId = Long.valueOf(jwtUtil.extractUserId(token));

        // Validate input data
        if (userId == null || favoriteRequestDTO.getProduct_id() == null || favoriteRequestDTO.getCategory_id() == null) {
            signUpResponseDTO.setMessage("Please fill missing data");
            signUpResponseDTO.setStatusCode((long) -1);
            return signUpResponseDTO;
        }

        // Check if Favorite already exists
        Optional<Favorite> favoriteExist = favoriteRepository.findById(userId);
        if (favoriteExist.isPresent()) {
            signUpResponseDTO.setMessage("Favorite already exists");
            signUpResponseDTO.setStatusCode((long) -2);
            return signUpResponseDTO;
        }

        // Check if User exists
        Optional<User> userExist = userRepository.findById(userId);
        if (userExist.isEmpty()) {
            signUpResponseDTO.setMessage("Selected User doesn't exist");
            signUpResponseDTO.setStatusCode((long) -3);
            return signUpResponseDTO;
        }

        // Check if Product exists
        Optional<Product> productExist = productRepository.findById(favoriteRequestDTO.getProduct_id());
        if (productExist.isEmpty()) {
            signUpResponseDTO.setMessage("Selected Product doesn't exist");
            signUpResponseDTO.setStatusCode((long) -4);
            return signUpResponseDTO;
        }

        // Check if Category exists
        Optional<Category> categoryExist = categoryRepository.findById(favoriteRequestDTO.getCategory_id());
        if (categoryExist.isEmpty()) {
            signUpResponseDTO.setMessage("Selected Category doesn't exist");
            signUpResponseDTO.setStatusCode((long) -5);
            return signUpResponseDTO;
        }

        // Create new Favorite entity
        Favorite newFavorite = convertToEntity(favoriteRequestDTO, userExist.get(), productExist.get(), categoryExist.get());

        // Save Favorite entity
        favoriteRepository.save(newFavorite);

        // Set successful response
        signUpResponseDTO.setMessage("Favorite added successfully");
        signUpResponseDTO.setStatusCode(0L);
        return signUpResponseDTO;
    }

    public List<FavoriteResponseDTO> getAllFavoritesByUserId(Long userId) {
        List<Favorite> favorites = favoriteRepository.findAllByUser_Userid(userId);
        if (favorites.isEmpty()) {
            logger.info("No favorites found for user ID: " + userId);
        }
        return favorites.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteFavorite(Long favoriteId) {
        Optional<Favorite> favoriteExist = favoriteRepository.findById(favoriteId);
        if (favoriteExist.isEmpty()) {
            logger.warning("Favorite not found with ID: " + favoriteId);
            throw new RuntimeException("Favorite not found with ID: " + favoriteId);
        }
        favoriteRepository.deleteById(favoriteId);
    }
}
