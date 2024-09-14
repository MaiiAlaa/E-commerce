package org.example.e_commerce.Service;

import jakarta.transaction.Transactional;
import org.example.e_commerce.Entity.Cart;
import org.example.e_commerce.Entity.CartDetails;
import org.example.e_commerce.Entity.Product;
import org.example.e_commerce.Repository.CartDetailsRepo;
import org.example.e_commerce.Repository.CartRepo;
import org.example.e_commerce.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RemoveProductAfterPurchase {

    @Autowired
    private CartDetailsRepo cartDetailsRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private CartRepo cartRepo;

    @Transactional
    public void removeProductIfQuantityZero(Long userId, Long productId, int purchasedQuantity) {
        // Retrieve the user's cart
        Cart cart = cartRepo.findByUserid(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        // Retrieve the CartDetails for the given product
        CartDetails cartDetails = cartDetailsRepo.findByCartAndProduct(cart, productRepo.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found")))
                .orElseThrow(() -> new RuntimeException("Product not found in cart"));

        // Update the quantity and remove if necessary
        int updatedQuantity = cartDetails.getQuantity() - purchasedQuantity;

        if (updatedQuantity <= 0) {
            // If quantity is zero or less, remove the product from the cart
            cartDetailsRepo.delete(cartDetails);
        } else {
            // Otherwise, update the quantity and amount in the cart
            cartDetails.setQuantity(updatedQuantity);
            cartDetails.setAmount(updatedQuantity * cartDetails.getProduct().getPrice());
            cartDetailsRepo.save(cartDetails);
        }
    }
}
