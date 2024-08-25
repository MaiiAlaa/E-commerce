package org.example.e_commerce.Controller;

import org.example.e_commerce.Service.FavoriteService;
import org.example.e_commerce.dto.dtoRequest.FavoriteRequestDTO;
import org.example.e_commerce.dto.dtoResponse.FavoriteResponseDTO;
import org.example.e_commerce.dto.dtoResponse.SignUpResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/add")
    public ResponseEntity<SignUpResponseDTO> addFavorite(@RequestHeader("Authorization") String token,
                                                         @RequestBody FavoriteRequestDTO favoriteRequestDTO) {
        SignUpResponseDTO response = favoriteService.addFavorite(token, favoriteRequestDTO);
        HttpStatus status = response.getStatusCode() == 0 ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FavoriteResponseDTO>> getAllFavoritesByUserId(@PathVariable("userId") Long userId) {
        List<FavoriteResponseDTO> favorites = favoriteService.getAllFavoritesByUserId(userId);
        if (favorites.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(favorites, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{favoriteId}")
    public ResponseEntity<String> deleteFavorite(@PathVariable("favoriteId") Long favoriteId) {
        try {
            favoriteService.deleteFavorite(favoriteId);
            return new ResponseEntity<>("Favorite deleted successfully", HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
