package org.example.e_commerce.dto.dtoResponse;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter

public class ProductDetailsDTO extends ProductDTO {
    private String description ;
    private List<String> imageUrls;


    public ProductDetailsDTO(Long id, String name, Double price, Integer stock, Long categoryId, String categoryName, String mainImageUrl,String description, List<String> imageUrls) {
        super(id, name, price, stock, categoryId, categoryName, mainImageUrl);
        this.description = description ;
        this.imageUrls = imageUrls;
    }

    public ProductDetailsDTO() {
        super();
    }
}
