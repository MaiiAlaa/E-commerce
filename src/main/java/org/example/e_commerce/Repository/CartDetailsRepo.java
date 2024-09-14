package org.example.e_commerce.Repository;

import org.example.e_commerce.Entity.Cart;
import org.example.e_commerce.Entity.CartDetails;
import org.example.e_commerce.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface CartDetailsRepo extends JpaRepository<CartDetails, Long> {
    List<CartDetails> findByCart(Cart cart);
    Optional<CartDetails> findByCartAndProduct(Cart cart, Product product);

    List<CartDetails> findByProduct(Product product);

    // New method to delete all CartDetails related to a specific cart
    void deleteByCart(Cart cart);
}
