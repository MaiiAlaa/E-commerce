package org.example.e_commerce.Repository;

import org.example.e_commerce.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByProductName(String productName);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> findProductCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchString, '%')) OR LOWER(p.manufacturer) LIKE LOWER(CONCAT('%', :searchString, '%'))")
    List<Product> searchByNameOrManufacturer(@Param("searchString") String searchString);
}

