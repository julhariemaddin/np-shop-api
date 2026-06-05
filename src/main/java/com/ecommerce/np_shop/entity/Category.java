package com.ecommerce.np_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NonNull
    private String categoryName;
}
