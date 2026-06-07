package com.ecommerce.np_shop.dto.api.v1;

import com.ecommerce.np_shop.entity.OrderItem;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {
    private UUID orderId;
    private Integer totalItemsQuantity;
    private UUID accountId;
    private List<OrderItemRespond> orderItems;
    private LocalDateTime createdAt;
    private PaymentRespond payment;
    private Double totalPrice;
}
