package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product,Long> {
 public Product findByProductName(String productName);

 List<Product> findByProductId(long id);


 @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
 List<Product> findProductCategoryId(@Param("categoryId") Long categoryId);

 @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchString, '%')) OR LOWER(p.manufacturer) LIKE LOWER(CONCAT('%', :searchString, '%'))")
 List<Product> searchByNameOrManufacturer(@Param("searchString") String searchString);
}
