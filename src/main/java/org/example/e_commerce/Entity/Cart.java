package org.example.e_commerce.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cart")
public class Cart {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Id
    private Long cart_id;
    private Long userid;

//    @OneToOne
//   @JoinColumn(name = "userid", unique = true)
//    private User user;


}
