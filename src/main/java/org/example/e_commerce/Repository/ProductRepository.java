package org.example.e_commerce.Repository;

import org.example.e_commerce.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory_Categoryid(Long categoryid);  // Use 'categoryid' to match your column name
}
