package com.ecommerce.np_shop.service.serviceImpl;

import com.ecommerce.np_shop.dto.api.v1.Cart;
import com.ecommerce.np_shop.dto.api.v1.CartItem;
import com.ecommerce.np_shop.dto.api.v1.CartItemRequest;
import com.ecommerce.np_shop.entity.Product;
import com.ecommerce.np_shop.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {
    private final RedisTemplate<String,Object> redisTemplate;
    private final String CART_PREFIX = "cart:";
    private final ProductServiceImpl productService;
    @Override
    public Cart getCart(UUID userId) {
        if(!hasCart(userId)) {
            return createCart(userId);
        }
        Object object =  redisTemplate.opsForValue().get(CART_PREFIX + userId);
        if(!(object instanceof  Cart cart)) {
            throw  new RuntimeException("Cart not found for user : " + userId.toString() );
        }
        return cart;
    }

    @Override
    public Cart addItem(UUID userId, CartItemRequest cartItemRequest) {
        Cart cart;
        if(!hasCart(userId)) {
            cart  = createCart(userId);
        }else {
            cart = getCart(userId);
        }

        Product product = productService.checkProductExistsAndGetProduct(cartItemRequest.getProductId());
        cart.getCartItemList().stream().filter(item -> item.getProductId().equals(cartItemRequest.getProductId()))
                .findFirst().ifPresentOrElse(
                        item -> item.setProductQuantity(item.getProductQuantity()+cartItemRequest.getProductQuantity()),
                        () -> cart.getCartItemList().add(
                                CartItem.builder()
                                        .productName(product.getName())
                                        .productId(product.getId())
                                        .productPrice(product.getPrice())
                                        .productQuantity(cartItemRequest.getProductQuantity())
                                        .build()
                        )
                );
        return cart;
    }
    private String getCART_PREFIX(UUID userId) {
        return CART_PREFIX + userId.toString();
    }
    private boolean hasCart(UUID userId) {
        return redisTemplate.hasKey(CART_PREFIX + userId);
    }
    private Cart createCart(UUID userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        return cart;
    }

}
