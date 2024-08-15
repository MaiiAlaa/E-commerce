package org.example.e_commerce.dto.dtoResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.e_commerce.Entity.Category;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDTO {

    private String message;
    private Long statusCode;
    private Category category;
}
