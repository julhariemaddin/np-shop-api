package com.ecommerce.np_shop.dto.api.v1;


import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private ImageResponse mainImage;
    private List<ImageResponse> images = new ArrayList<>();
    private UUID categoryId;
    private LocalDateTime createdAt;
    private int numberOfReviews;
    private double overAllRating;
}
