package org.example.e_commerce.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cartDetails")
public class CartDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartDetails_id")
    private Long cartDetailsId;

    @OneToOne
    @JoinColumn(name = "cart_id", referencedColumnName = "cart_id", foreignKey = @ForeignKey(name = "fk_cart_cartDetails"))
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "productid", foreignKey = @ForeignKey(name = "fk_product_cartDetails"))
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private Double amount;
}
