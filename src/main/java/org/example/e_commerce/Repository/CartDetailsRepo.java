package org.example.e_commerce.Repository;

import org.example.e_commerce.Entity.Cart;
import org.example.e_commerce.Entity.CartDetails;
import org.example.e_commerce.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartDetailsRepo extends JpaRepository<CartDetails, Long> {

    // Find all cart details for a specific cart
    List<CartDetails> findByCart(Cart cart);

    // Find specific cart detail based on cart and product
    Optional<CartDetails> findByCartAndProduct(Cart cart, Product product);

    // Find cart details based on a product
    List<CartDetails> findByProduct(Product product);

    // Delete all cart details for a specific cart
    void deleteByCart(Cart cart);

    // Delete specific cart details entry by product and cart (useful for remove product from cart feature)
    void deleteByCartAndProduct(Cart cart, Product product);

    List<CartDetails> findByCartAndIsPurchasedFalse(Cart cart);
}
