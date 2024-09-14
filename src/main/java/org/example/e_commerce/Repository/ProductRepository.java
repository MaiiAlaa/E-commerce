package org.example.e_commerce.Repository;

import org.example.e_commerce.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find a single product by its name
    Optional<Product> findByProductName(String productName);

    // Find products by their ID (usually findById is enough)
    // Consider removing or renaming if not needed
    @Override
    Optional<Product> findById(Long id);

    // Find products by category ID using a custom query
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> findProductByCategoryId(@Param("categoryId") Long categoryId);

    // Search products by name or manufacturer using a custom query
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :searchString, '%')) OR LOWER(p.manufacturer) LIKE LOWER(CONCAT('%', :searchString, '%'))")
    List<Product> searchByNameOrManufacturer(@Param("searchString") String searchString);
}
