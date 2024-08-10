package org.example.e_commerce.dto.dtoRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartDetailsRequestDTO {

    @NotNull(message = "Cart ID is mandatory")
    private Long cartId;

    @NotNull(message = "Product ID is mandatory")
    private Long productId;

    @NotNull(message = "Quantity is mandatory")
    private Integer quantity;

    @NotNull(message = "Amount is mandatory")
    private BigDecimal amount;
}
