package com.ecommerce.np_shop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;
    private String url;
    private String fileName;
    private String contentType;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
