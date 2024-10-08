/*package org.example.e_commerce.Controller;

import org.example.e_commerce.Service.FavoriteService;
import org.example.e_commerce.Service.UserService;
import org.example.e_commerce.Service.ProductService;
import org.example.e_commerce.Service.CategoryService;
import org.example.e_commerce.dto.dtoRequest.FavoriteRequestDTO;
import org.example.e_commerce.Entity.User;
import org.example.e_commerce.Entity.Product;
import org.example.e_commerce.Entity.Category;
import org.example.e_commerce.util.JwtUtil; // Add this import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final JwtUtil jwtUtil; // Add this field

    @Autowired
    public FavoriteController(FavoriteService favoriteService, UserService userService,
                              ProductService productService, CategoryService categoryService, JwtUtil jwtUtil) {
        this.favoriteService = favoriteService;
        this.userService = userService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.jwtUtil = jwtUtil; // Initialize the JwtUtil
    }

    @PostMapping("/add")
    public ResponseEntity<FavoriteRequestDTO> addFavorite(
            @Valid @RequestBody FavoriteRequestDTO favoriteRequestDTO,
            @RequestHeader("Authorization") String token) {

        try {
            // Extract user ID from the token
            String jwtToken = token.replace("Bearer ", ""); // Remove "Bearer " prefix if present
            Long userId = jwtUtil.extractUserId(jwtToken);
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            System.out.println("Retrieved User: " + user);

            // Fetch product and handle the response map
            Map<String, Object> productResponse = productService.getProductById(favoriteRequestDTO.getProductId());
            if ((Long) productResponse.get("statusCode") == -1) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Product product = (Product) productResponse.get("product");
            System.out.println("Product Response: " + productResponse);

            // Fetch category and handle the response map
            Map<String, Object> categoryResponse = (Map<String, Object>) categoryService.getCategoryById(favoriteRequestDTO.getCategoryId());
            if ((Long) categoryResponse.get("statusCode") == -1) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Category category = (Category) categoryResponse.get("category");
            System.out.println("Category Response: " + categoryResponse);

            // Update favoriteRequestDTO with user ID
            favoriteRequestDTO.setUserId(userId);

            FavoriteRequestDTO savedFavorite = favoriteService.saveFavorite(favoriteRequestDTO, user, product, category);
            return new ResponseEntity<>(savedFavorite, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FavoriteRequestDTO>> getAllFavoritesByUserId(@PathVariable Long userId) {
        try {
            List<FavoriteRequestDTO> favorites = favoriteService.getAllFavoritesByUserId(userId);
            return new ResponseEntity<>(favorites, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{favoriteId}")
    public ResponseEntity<FavoriteRequestDTO> getFavoriteById(@PathVariable Long favoriteId) {
        try {
            Optional<FavoriteRequestDTO> favorite = favoriteService.getFavoriteById(favoriteId);
            return favorite.map(f -> new ResponseEntity<>(f, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{favoriteId}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long favoriteId) {
        try {
            favoriteService.deleteFavorite(favoriteId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
*/