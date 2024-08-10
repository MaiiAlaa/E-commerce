package org.example.e_commerce.dto.dtoRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequestDTO {
    @NotBlank(message = "Product name is mandatory")
    private String name;

    @NotNull(message = "Price is mandatory")
    private Double price;

    @NotNull(message = "Category ID is mandatory")
    private Long categoryid;

    private Integer stock;
    private String description;
}
