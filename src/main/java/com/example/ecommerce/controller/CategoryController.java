package com.example.ecommerce.controller;


import com.example.ecommerce.dto.response.SignUpResponseDTO;
import com.example.ecommerce.dto.CategoryDTO;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {


    @Autowired
    private CategoryService categoryService;

    @PostMapping("/add")
    public ResponseEntity<SignUpResponseDTO> addCategory(@Valid @RequestBody CategoryDTO categoryDTO)  {// req body coming from postman

        SignUpResponseDTO response = categoryService.addCategory(categoryDTO);

        if(response.getStatusCode() != 0l){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public List<Category> getAllCategory() {
        return categoryService.getAllCategory();
    }


}
