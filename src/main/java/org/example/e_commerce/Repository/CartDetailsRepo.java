package org.example.e_commerce.Repository;

import org.example.e_commerce.Entity.Cart;
import org.example.e_commerce.Entity.CartDetails;
import org.example.e_commerce.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartDetailsRepo extends JpaRepository<CartDetails, Long> {
    // Find all CartDetails for a given Cart
    List<CartDetails> findByCart(Cart cart);

    // Find specific CartDetails by Cart and Product
    Optional<CartDetails> findByCartAndProduct(Cart cart, Product product);
}
