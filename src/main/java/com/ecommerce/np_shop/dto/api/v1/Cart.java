package com.ecommerce.np_shop.dto.api.v1;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Cart {
    private UUID userId;
    private List<CartItem> cartItemList = new ArrayList<>();
    public double getTotal(){
        return cartItemList.stream()
                .mapToDouble(
                        item -> item.getProductPrice() * item.getProductQuantity()
                ).sum();
    }

}
