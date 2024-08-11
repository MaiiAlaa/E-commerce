package org.example.e_commerce.Service;



import org.example.e_commerce.Entity.Category;
import org.example.e_commerce.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepo;

    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepo.findById(id).orElse(null);
    }

    public Category createCategory(Category category) {
        return categoryRepo.save(category);
    }

    public Category updateCategory(Long id, Category category) {
        if (categoryRepo.existsById(id)) {
            category.setCategoryid(id);
            return categoryRepo.save(category);
        }
        return null;
    }

    public void deleteCategory(Long id) {
        categoryRepo.deleteById(id);
    }
}
