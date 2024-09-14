//package org.example.e_commerce.Entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "transaction")
//@Data
//public class Transaction {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long transactionId;
//
//    @ManyToOne
//    @JoinColumn(name = "cartDetails_id")
//    private CartDetails cartDetails;
//
//    private String invoiceNumber;
//
//    private LocalDateTime date;
//
//    private String orderDescription;
//
//    private int quantity;
//
//    private double amount;
//}
package org.example.e_commerce.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Entity
@Table(name = "transaction")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "cartdetails_id")
    private CartDetails cartDetails;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "order_description")
    private String orderDescription;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "amount")
    private double amount;
}
