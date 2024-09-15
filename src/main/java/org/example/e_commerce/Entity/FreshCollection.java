package org.example.e_commerce.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "fresh_collection")
public class FreshCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long collection_id;

    @Column(nullable = false)
    private String name;

    @Column
    private String image;

    @Column
    private String description;

}

