package org.example.e_commerce.Service;

import org.example.e_commerce.Entity.Category;
import org.example.e_commerce.Repository.CategoryRepository;
import org.example.e_commerce.dto.dtoResponse.CategoryResponseDTO;
import org.example.e_commerce.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepo;

    JwtUtil jwtUtil = new JwtUtil();
    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    public CategoryResponseDTO getCategoryById(Long id) {
        Optional<Category> category = categoryRepo.findById(id);
        if (category.isPresent()) {
            return new CategoryResponseDTO("Category found", 200L, category.get());
        } else {
            return new CategoryResponseDTO("Category ID " + id + " not found", 404L, null);
        }
    }

    public CategoryResponseDTO createCategory(Category category , String token) {
        String role = jwtUtil.extractRole(token);
        System.out.println(role);
        Category savedCategory = categoryRepo.save(category);
        if (role.equals("USER")){
            return new CategoryResponseDTO("You do not have the necessary permissions to perform this action.", 403L, null);
        }

        if (role == null || role.isEmpty()) {
            throw new IllegalArgumentException("Role must be provided");
        }
        return new CategoryResponseDTO("Category created successfully", 201L, savedCategory);
    }

    public CategoryResponseDTO updateCategory(Long id, Category category , String token) {

        String role = jwtUtil.extractRole(token);
        if (role.equals("USER")){
            return new CategoryResponseDTO("You do not have the necessary permissions to perform this action.", 403L, null);
        }
        if (categoryRepo.existsById(id)) {
            category.setCategoryid(id);
            Category updatedCategory = categoryRepo.save(category);
            return new CategoryResponseDTO("Category updated successfully", 200L, updatedCategory);
        } else {
            return new CategoryResponseDTO("Category ID " + id + " not found", 404L, null);
        }
    }

    public CategoryResponseDTO deleteCategory(Long id , String token) {
        String role = jwtUtil.extractRole(token);
        if (role.equals("USER")){
            return new CategoryResponseDTO("You do not have the necessary permissions to perform this action.", 403L, null);
        }
        if (categoryRepo.existsById(id)) {
            categoryRepo.deleteById(id);
            return new CategoryResponseDTO("Category deleted successfully", 200L, null);
        } else {
            return new CategoryResponseDTO("Category ID " + id + " not found", 404L, null);
        }
    }
}