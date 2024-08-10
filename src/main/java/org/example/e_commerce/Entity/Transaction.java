package org.example.e_commerce.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "cartDetails_id", referencedColumnName = "cartDetails_id", foreignKey = @ForeignKey(name = "fk_cartDetails_transaction"))
    private CartDetails cartDetails;

    @Column(name = "InvoiceNumber", nullable = false, length = 50)
    private String invoiceNumber;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "order_description", nullable = false, columnDefinition = "TEXT")
    private String orderDescription;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private Double amount;
}
