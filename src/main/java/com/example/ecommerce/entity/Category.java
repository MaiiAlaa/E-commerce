package com.example.ecommerce.entity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "category")  // Table name should match the SQL table name
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")  // Column name should match the SQL column name
    private Long categoryId;

    @Column(name = "category_name", nullable = false, length = 255)
    private String categoryName;
}