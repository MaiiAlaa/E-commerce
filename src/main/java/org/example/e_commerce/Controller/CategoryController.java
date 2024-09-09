package org.example.e_commerce.Controller;

import org.example.e_commerce.Entity.Category;
import org.example.e_commerce.Service.CategoryService;
import org.example.e_commerce.dto.dtoRequest.CategoryRequestDTO;
import org.example.e_commerce.dto.dtoResponse.CategoryResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        CategoryResponseDTO response = categoryService.getCategoryById(id);
        if (response.getCategory() == null) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody Category category, @RequestHeader("Authorization") String token) {
        CategoryResponseDTO response = categoryService.createCategory(category, token);
        HttpStatus status = determineHttpStatus(response);
        return new ResponseEntity<>(response, status);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Category category) {
        CategoryResponseDTO response = categoryService.updateCategory(id, category, token);
        HttpStatus status = determineHttpStatus(response);
        return new ResponseEntity<>(response, status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> deleteCategory(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        CategoryResponseDTO response = categoryService.deleteCategory(id, token);
        HttpStatus status = determineHttpStatus(response);
        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/stores")
    public ResponseEntity<List<CategoryRequestDTO>> getAllCategoryNamesAndImages() {
        List<CategoryRequestDTO> response = categoryService.stores();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private HttpStatus determineHttpStatus(CategoryResponseDTO response) {
        if (response.getStatusCode() == 200L) {
            return HttpStatus.OK;
        } else if (response.getStatusCode() == 201L) {
            return HttpStatus.CREATED;
        } else if (response.getStatusCode() == 403L) {
            return HttpStatus.FORBIDDEN;
        } else if (response.getStatusCode() == 404L) {
            return HttpStatus.NOT_FOUND;
        } else {
            return HttpStatus.BAD_REQUEST; // For any other status codes, return a BAD REQUEST
        }
    }
}
