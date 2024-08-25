package org.example.e_commerce.dto.dtoRequest;

import lombok.Data;

@Data
public class FavoriteRequestDTO {
    private Long product_id;
    private Long category_id;
}