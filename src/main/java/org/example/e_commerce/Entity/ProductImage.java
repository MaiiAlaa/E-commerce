package org.example.e_commerce.Entity;

import jakarta.persistence.*;
import lombok.Data;

    @Data
    @Entity
    @Table(name = "product_images")
    public class ProductImage {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "imageid")
        private Long imageId;

        @Column(name = "imageurl", nullable = false)
        private String imageUrl;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "productid", nullable = false)
        private Product product;
    }
