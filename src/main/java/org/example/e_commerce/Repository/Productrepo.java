package org.example.e_commerce.Repository;
import org.example.e_commerce.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Productrepo extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
}
