package com.ecommerce.np_shop.dto.api.v1;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OrderItemRespond {
    private UUID id;
    private UUID orderId;
    private UUID productId;
    private Boolean productStatus;
    private int quantity;
    private double price;

    public double getTotalPrice() {
        return price * quantity;
    }
}
