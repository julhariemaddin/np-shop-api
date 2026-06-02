package com.ecommerce.np_shop.entity;

import jakarta.persistence.*;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NonNull
    private String name;
    @NonNull
    private String description;
    private int stock;
    private double price;
    @NonNull
    private String imageUrl;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
