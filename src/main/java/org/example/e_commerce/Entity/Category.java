package org.example.e_commerce.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryid;

    @NotBlank(message = "Category name is mandatory")
    @Column(name = "name", unique = true)
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference  // Allows serialization of products in this list
    private List<Product> products;

    @NotBlank(message = "Category Image is mandatory")
    @Column(name = "imageurl")
    private String image_url;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "discount", nullable = false, precision = 5, scale = 2)
    private Double discount;

    @Column(name = "market_image")
    private String marketImage;
}