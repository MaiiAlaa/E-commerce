package org.example.e_commerce.dto.dtoResponse;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter

public class ProductDetailsDTO extends ProductDTO {
    private String description ;
    private long categoryid ;
    private List<String> imageUrls;


    public ProductDetailsDTO(Long id, String name, Double price, Integer stock, Long categoryId, String categoryName, String mainImageUrl,String description,long categoryid, List<String> imageUrls) {
        super(id, name, price, stock, categoryId, categoryName, mainImageUrl);
        this.description = description ;
        this.categoryid = categoryid;
        this.imageUrls = imageUrls;
    }

    public ProductDetailsDTO() {
        super();
    }
}
