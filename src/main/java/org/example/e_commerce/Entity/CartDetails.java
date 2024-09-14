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
@Data
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

    @Column(name = "is_purchased", nullable = false)
    private boolean isPurchased = false;


}
