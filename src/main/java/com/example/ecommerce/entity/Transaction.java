package com.example.ecommerce.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "cartDetails_id")
    private CartDetails cartDetails;

    private String invoiceNumber;

    private LocalDateTime date;

    private String orderDescription;

    private int quantity;

    private double amount;
}

