//package org.example.e_commerce.Repository;
//
//import org.example.e_commerce.Entity.Cart;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface CartRepo extends JpaRepository<Cart, Long> {
//    Optional<Cart> findByUserid(Long userId);
//}
//
package org.example.e_commerce.Repository;

import org.example.e_commerce.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepo extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserid(Long userId);
}
