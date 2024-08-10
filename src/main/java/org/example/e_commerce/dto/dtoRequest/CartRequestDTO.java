package org.example.e_commerce.dto.dtoRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRequestDTO {

    @NotNull(message = "User ID is mandatory")
    private Long userId;
}
