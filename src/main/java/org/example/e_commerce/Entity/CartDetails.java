package org.example.e_commerce.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cartDetails")
@Data
public class CartDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartDetailsId;

    @OneToOne
    @JoinColumn(name = "cart_id", unique = true)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private double amount;
}
