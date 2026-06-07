package com.ecommerce.np_shop.service;

import com.ecommerce.np_shop.dto.api.v1.Cart;
import com.ecommerce.np_shop.dto.api.v1.CartItemRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface CartService {
     Cart getCart(UUID userId);
     Cart addItem(UUID userId , CartItemRequest cartItemRequest);
     void deleteCart(UUID userId);
     void deleteItem(UUID userId, UUID productId);
}
