package com.ecommerce.np_shop.dto.api.v1;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CartItem {
    private UUID productId;
    private String productName;
    private double productPrice;
    private int productQuantity;
}
