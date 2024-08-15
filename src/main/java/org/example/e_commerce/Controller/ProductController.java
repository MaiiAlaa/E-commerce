package org.example.e_commerce.Controller;

import jakarta.validation.Valid;
import org.example.e_commerce.Service.ProductService;
import org.example.e_commerce.dto.dtoRequest.ProductRequestDTO;
import org.example.e_commerce.dto.dtoResponse.ProductResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ProductResponseDTO getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping
    public List<ProductResponseDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping("/update")
    public ResponseEntity<ProductResponseDTO> updateProduct(@Valid @RequestBody ProductRequestDTO productDTO) {
        ProductResponseDTO response = productService.updateProduct(productDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(@RequestParam String search) {
        List<ProductResponseDTO> products = productService.searchProducts(search);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }

    @PostMapping("/add")
    public ResponseEntity<ProductResponseDTO> addProduct(@Valid @RequestBody ProductRequestDTO productDTO) {
        ProductResponseDTO response = productService.addProduct(productDTO);
        return ResponseEntity.ok(response);
    }
}