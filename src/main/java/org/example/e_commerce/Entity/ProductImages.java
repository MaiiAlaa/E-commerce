package org.example.e_commerce.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "product_images")
public class ProductImages {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "imageid")
        private Long imageId;

        @Column(name = "imageurl", nullable = false)
        private String imageUrl;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "productid", nullable = false)
        @JsonBackReference
        private Product product;

}
