package org.example.e_commerce.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productid;

    @NotBlank(message = "Product name is mandatory")
    @Column(name = "name")
    private String name;

    @NotNull(message = "Price is mandatory")
    @Column(name = "price")
    private Double price;

    @NotNull(message = "Stock is mandatory")
    @Column(name = "stock")
    private Integer stock;

    @Column(name = "description")
    private String description;

    // Establishing the many-to-one relationship with Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryid", nullable = false)
    private Category category;

    public void setProductid(long productid) {
    }
}
