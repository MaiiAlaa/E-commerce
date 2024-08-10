package org.example.e_commerce.Service;

import org.example.e_commerce.Entity.Category;
import org.example.e_commerce.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImp(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category saveCategory(Category category) {
        try {
            return categoryRepository.save(category);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving category: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Category> getCategoryById(long categoryid) {
        try {
            return categoryRepository.findById(categoryid);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving category by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Category> getAllCategories() {
        try {
            return categoryRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving all categories: " + e.getMessage(), e);
        }
    }

    @Override
    public Category updateCategory(long categoryid, Category category) {
        try {
            if (categoryRepository.existsById(categoryid)) {
                category.setCategoryid(categoryid); // Ensure the correct category ID is set
                return categoryRepository.save(category);
            }
            throw new RuntimeException("Category not found with ID: " + categoryid);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating category: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteCategory(long categoryid) {
        try {
            categoryRepository.deleteById(categoryid);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting category: " + e.getMessage(), e);
        }
    }
}
