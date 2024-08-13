package org.example.e_commerce.dto.dtoRequest;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private Long transactionId;
    private Long cartDetailsId;
    private String invoiceNumber;
    private LocalDateTime date;
    private String orderDescription;
    private int quantity;
    private double amount;
}
