package org.example.e_commerce.Controller;

import jakarta.validation.Valid;
import org.example.e_commerce.Entity.Product;
import org.example.e_commerce.Service.ProductService;
import org.example.e_commerce.dto.dtoRequest.ProductRequestDTO;
import org.example.e_commerce.dto.dtoResponse.SignUpResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long id) {
        Map<String, Object> response = productService.getProductById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<SignUpResponseDTO> updateProduct(@Valid @RequestBody ProductRequestDTO productDTO) {

        SignUpResponseDTO response = productService.updateProduct(productDTO);

        if (response.getStatusCode() != 0l) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<SignUpResponseDTO> addProduct(@Valid @RequestBody ProductRequestDTO  productDTO) {// req body coming from postman

        SignUpResponseDTO response = productService.addProduct(productDTO);

        if (response.getStatusCode() != 0l) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by_category/{categoryId}")
    public ResponseEntity<List<Product>> findProductCategoryId(@PathVariable Long categoryId) {
        List<Product> products = productService.findProductCategoryId(categoryId);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        Map<String, Object> response = productService.getAllProducts();
        return new ResponseEntity<>(response, HttpStatus.OK);
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


