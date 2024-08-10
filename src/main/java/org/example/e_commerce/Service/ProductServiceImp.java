package org.example.e_commerce.Service;

import org.example.e_commerce.Entity.Product;
import org.example.e_commerce.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImp(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product saveProduct(Product product) {
        try {
            return productRepository.save(product);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving product: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Product> getProductById(long productid) {
        try {
            return productRepository.findById(productid);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving product by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Product> getAllProducts() {
        try {
            return productRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving all products: " + e.getMessage(), e);
        }
    }

    @Override
    public Product updateProduct(long productid, Product product) {
        try {
            if (productRepository.existsById(productid)) {
                product.setProductid(productid); // Ensure the correct product ID is set
                return productRepository.save(product);
            }
            throw new RuntimeException("Product not found with ID: " + productid);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating product: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteProduct(long productid) {
        try {
            productRepository.deleteById(productid);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting product: " + e.getMessage(), e);
        }
    }
}
