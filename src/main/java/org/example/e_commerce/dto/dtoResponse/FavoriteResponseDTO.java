package org.example.e_commerce.dto.dtoResponse;

import lombok.Data;

@Data
public class FavoriteResponseDTO {
    private Long user_id;
    private Long cart_id;
    private Long product_id;
}
