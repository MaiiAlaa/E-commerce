package org.example.e_commerce.Controller;

import jakarta.validation.Valid;
import org.example.e_commerce.Entity.Product;
import org.example.e_commerce.Service.ProductService;
import org.example.e_commerce.dto.dtoRequest.ProductRequestDTO;
import org.example.e_commerce.dto.dtoResponse.ProductsResponseDTO;
import org.example.e_commerce.dto.dtoResponse.SignUpResponseDTO;
import org.example.e_commerce.dto.dtoResponse.cartProductDetailsDTO;
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
    public ResponseEntity<ProductsResponseDTO> getProductById(@PathVariable Long id) {
        ProductsResponseDTO response = productService.getProductById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<SignUpResponseDTO> addProduct(@Valid @RequestBody ProductRequestDTO productDTO, @RequestHeader("Authorization") String token) {
        SignUpResponseDTO response = productService.addProduct(productDTO, token);

        if (response.getStatusCode() != 0L) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<SignUpResponseDTO> updateProduct(@Valid @RequestBody ProductRequestDTO productDTO, @RequestHeader("Authorization") String token) {
        SignUpResponseDTO response = productService.updateProduct(productDTO, token);

        if (response.getStatusCode() != 0L) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SignUpResponseDTO> deleteProduct(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        SignUpResponseDTO response = productService.deleteProduct(id, token);

        if (response.getStatusCode() != 0L) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by_category/{categoryId}")
    public ResponseEntity<List<Product>> findProductByCategoryId(@PathVariable Long categoryId) {
        List<Product> products = productService.findProductByCategoryId(categoryId);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }



    public ResponseEntity<List<cartProductDetailsDTO>> getAllProducts(
            @RequestHeader("Authorization") String token) {
        List<cartProductDetailsDTO> products = productService.getAllProductsWithCartSize(token);
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