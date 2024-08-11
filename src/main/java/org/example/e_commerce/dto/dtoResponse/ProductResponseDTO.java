package org.example.e_commerce.dto.dtoResponse;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductResponseDTO {
    private Long id;
    private String name;
    private Long category_id;

    public ProductResponseDTO(Long id, String name, Long category_id) {
        this.id = id;
        this.name = name;
        this.category_id = category_id;
    }
}
