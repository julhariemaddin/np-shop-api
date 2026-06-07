package com.ecommerce.np_shop.service;

import com.ecommerce.np_shop.dto.api.v1.OrderResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface OrderService {
     OrderResponse createOrder(UUID userId);
     List<OrderResponse> getOrders(UUID userId);
     OrderResponse getOrder(UUID userId,UUID orderId);
     void deleteOrder(UUID userId,UUID orderId);
}

