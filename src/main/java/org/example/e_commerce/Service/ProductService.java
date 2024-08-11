package org.example.e_commerce.Service;

import org.example.e_commerce.Entity.Product;
import org.example.e_commerce.Repository.Productrepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final Productrepo productRepository;

    @Autowired
    public ProductService(Productrepo productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
}
