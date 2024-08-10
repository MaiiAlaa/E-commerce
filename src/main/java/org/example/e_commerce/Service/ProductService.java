package org.example.e_commerce.Service;

import org.example.e_commerce.Entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product saveProduct(Product product);
    Optional<Product> getProductById(long productid);
    List<Product> getAllProducts();
    Product updateProduct(long productid, Product product);
    void deleteProduct(long productid);
}
