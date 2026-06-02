package com.ecommerce.np_shop.dto.api.v1;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private UUID id;
    private String name;
    private String description;
    private int stock;
    private double price;
    private String imageUrl;
    private UUID categoryId;
}
