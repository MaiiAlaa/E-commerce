package org.example.e_commerce.Repository;

import org.example.e_commerce.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {

    // Find cart by user ID
    Optional<Cart> findByUserid(Long userid);

    // Add method to delete a cart if necessary
    void deleteByUserid(Long userid);  // Optional for flexibility, but you can also delete by Cart ID directly
}
