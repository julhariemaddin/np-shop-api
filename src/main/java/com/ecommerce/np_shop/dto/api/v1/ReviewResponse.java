package com.ecommerce.np_shop.dto.api.v1;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@Setter
public class ReviewResponse {
    private UUID id;
    private int rating;
    private String description;
    private UUID productId;
    private String accountUsername;
    private LocalDateTime createdAt;
}
