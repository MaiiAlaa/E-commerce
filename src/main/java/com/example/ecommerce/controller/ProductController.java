package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.dto.response.SignUpResponseDTO;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<SignUpResponseDTO> addProduct(@Valid @RequestBody ProductDTO productDTO) {// req body coming from postman

        SignUpResponseDTO response = productService.addProduct(productDTO);

        if (response.getStatusCode() != 0l) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<SignUpResponseDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO) {

        SignUpResponseDTO response = productService.updateProduct(productDTO);

        if (response.getStatusCode() != 0l) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable Long id) {
        List<Product> product = productService.getProductById(id);
        ProductDTO ProductDTO = new ProductDTO();
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by_category/{categoryId}")
    public ResponseEntity<List<Product>> findProductCategoryId(@PathVariable Long categoryId) {
        List<Product> products = productService.findProductCategoryId(categoryId);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String search) {
        List<Product> products = productService.searchProducts(search);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }

}
