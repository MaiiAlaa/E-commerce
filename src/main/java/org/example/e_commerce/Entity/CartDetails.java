//package org.example.e_commerce.Entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//@Entity
//@Table(name = "cartDetails")
//@Data
//public class CartDetails {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long cartDetailsId;
//
//    @OneToOne
//    @JoinColumn(name = "cart_id", unique = true)
//    private Cart cart;
//
//    @ManyToOne
//    @JoinColumn(name = "product_id")
//    private Product product;
//
//    private int quantity;
//
//    private double amount;
//}
package org.example.e_commerce.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cartdetails")
public class CartDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartdetails_id")
    private Long cartDetailsId;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "amount")
    private double amount;

    // Getters and Setters
    public Long getCartDetailsId() {
        return cartDetailsId;
    }

    public void setCartDetailsId(Long cartDetailsId) {
        this.cartDetailsId = cartDetailsId;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}

