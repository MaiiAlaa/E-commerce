package org.example.e_commerce.Entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "product")  // Table name lazm y match the SQL table name
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")  // Column name lazm y match the SQL column name
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "price")
    private Double price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;


    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "warranty_period")
    private Integer warrantyPeriod;


    @Column(name = "imageurl")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryid", nullable = false)  // Use 'categoryid' as per your table schema
    @JsonBackReference  // Prevents recursion by not serializing this side
    private Category category;
}
