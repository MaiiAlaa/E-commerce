package com.example.ecommerce.service;

import com.example.ecommerce.dto.response.SignUpResponseDTO;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.dto.CategoryDTO;
import com.example.ecommerce.repository.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {


    @Autowired
    private CategoryRepo categoryRepo;

    public SignUpResponseDTO addCategory(CategoryDTO categoryDTO) {

        SignUpResponseDTO responseDTO = new SignUpResponseDTO();
        if(categoryDTO.getCategoryName() == null){
            responseDTO.setMessage("Invalid category");
            responseDTO.setStatusCode(-1l);
            return responseDTO;
        }

        Category categoryExist = categoryRepo.findByCategoryName(categoryDTO.getCategoryName());
        if(categoryExist != null) {
            responseDTO.setMessage("category already exists");
            responseDTO.setStatusCode(-2l);
            return responseDTO;

        }

        Category categoryNew = new Category();
        categoryNew.setCategoryName(categoryDTO.getCategoryName());

        categoryRepo.save(categoryNew);

        responseDTO.setMessage("Added Successfully");
        responseDTO.setStatusCode(0l);
        return responseDTO;
    }


    public List<Category> getAllCategory() {
    return categoryRepo.findAll();
    }
}


