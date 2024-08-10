package org.example.e_commerce.dto.dtoRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionRequestDTO {

    @NotNull(message = "Cart Details ID is mandatory")
    private Long cartDetailsId;

    @NotBlank(message = "Invoice Number is mandatory")
    private String invoiceNumber;

    @NotNull(message = "Date is mandatory")
    private LocalDateTime date;

    @NotBlank(message = "Order Description is mandatory")
    private String orderDescription;

    @NotNull(message = "Quantity is mandatory")
    private Integer quantity;

    @NotNull(message = "Amount is mandatory")
    private BigDecimal amount;
}
