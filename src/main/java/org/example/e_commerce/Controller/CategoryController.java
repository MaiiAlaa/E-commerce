package org.example.e_commerce.Controller;

import org.example.e_commerce.Entity.Category;
import org.example.e_commerce.Service.CategoryService;
import org.example.e_commerce.dto.dtoResponse.CategoryResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public CategoryResponseDTO getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CategoryResponseDTO createCategory(@RequestBody Category category) {
        return categoryService.createCategory(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public CategoryResponseDTO updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return categoryService.updateCategory(id, category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public CategoryResponseDTO deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }
}
