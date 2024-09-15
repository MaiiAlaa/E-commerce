package org.example.e_commerce.dto.dtoRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequestDTO {
    private long store_id;
    @NotBlank(message = "Category name is mandatory")
    private String name;
    private String imageurl;
    private String description;
    private String market_image;
    private Double discount;

}
