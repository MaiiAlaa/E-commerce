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

@Data
@Entity
@Table(name = "cartdetails")  // Updated to match the table name
public class CartDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartdetails_id")  // Updated to match the column name
    private Long cartDetailsId;

    @ManyToOne
    @JoinColumn(name = "cart_id")  // Updated to match the column name
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")  // Updated to match the column name
    private Product product;

    @Column(name = "quantity")  // Updated to match the column name
    private int quantity;

    @Column(name = "amount")  // Updated to match the column name
    private double amount;
}
