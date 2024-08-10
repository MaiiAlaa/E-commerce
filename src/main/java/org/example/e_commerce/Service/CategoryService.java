package org.example.e_commerce.Service;

import org.example.e_commerce.Entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category saveCategory(Category category);
    Optional<Category> getCategoryById(long categoryid);
    List<Category> getAllCategories();
    Category updateCategory(long categoryid, Category category);
    void deleteCategory(long categoryid);
}
