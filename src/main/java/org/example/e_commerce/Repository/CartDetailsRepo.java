//package org.example.e_commerce.Repository;
//import org.example.e_commerce.Entity.Cart;
//import org.example.e_commerce.Entity.CartDetails;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//public interface CartDetailsRepo extends JpaRepository<CartDetails, Long> {
//    CartDetails findByCart(Cart cart);
//}
package org.example.e_commerce.Repository;

import org.example.e_commerce.Entity.Cart;
import org.example.e_commerce.Entity.CartDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartDetailsRepo extends JpaRepository<CartDetails, Long> {
    CartDetails findByCart(Cart cart);
}
