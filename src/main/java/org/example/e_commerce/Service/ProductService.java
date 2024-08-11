package org.example.e_commerce.Service;

import org.example.e_commerce.Entity.Product;
import org.example.e_commerce.Repository.ProductRepository;
import org.example.e_commerce.dto.dtoResponse.ProductResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponseDTO> getProductsByCategoryId(Long categoryId) {
        List<Product> products = productRepository.findByCategory_Categoryid(categoryId);
        return products.stream().map(product ->
                new ProductResponseDTO(
                        product.getProductid(),
                        product.getName(),
                        product.getPrice(),
                        product.getStock(),
                        product.getDescription(),
                        product.getCategory().getCategoryid(),
                        product.getCategory().getName()  // Assuming the Category entity has a 'name' field
                )
        ).collect(Collectors.toList());
    }

}
