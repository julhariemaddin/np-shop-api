package com.ecommerce.np_shop.redis.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
public class CartItem implements Serializable {
    private UUID productId;
    private String productName;
    private double productPrice;
    private int productQuantity;
}
