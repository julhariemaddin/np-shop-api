package com.ecommerce.np_shop.service.serviceImpl;

import com.ecommerce.np_shop.dto.api.v1.Cart;
import com.ecommerce.np_shop.dto.api.v1.CartItem;
import com.ecommerce.np_shop.dto.api.v1.CartItemRequest;
import com.ecommerce.np_shop.entity.Product;
import com.ecommerce.np_shop.service.CartService;
import java.time.Duration;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {
  private final RedisTemplate<String, Object> redisTemplate;
  private final String CART_PREFIX = "cart:";
  private final Duration duration = Duration.ofMinutes(30);
  private final ProductServiceImpl productService;

  @Override
  @PreAuthorize("hasRole('USER')")
  public Cart getCart(UUID userId) {
    if (!hasCart(userId)) {
      return createCart(userId);
    }
    Object object = redisTemplate.opsForValue().get(getCART_PREFIX(userId));

    return checkObjectAndGetCart(object);
  }

  @Override
  @PreAuthorize("hasRole('USER')")
  public Cart addItem(UUID userId, CartItemRequest cartItemRequest) {
    Cart cart;
    if (hasCart(userId)) {
      cart = getCart(userId);
    } else {
      cart = createCart(userId);
    }
    Product product =
        productService.checkProductExistsAndGetProduct(cartItemRequest.getProductId());
    cart.getCartItemList().stream()
        .filter(item -> item.getProductId().equals(cartItemRequest.getProductId()))
        .findFirst()
        .ifPresentOrElse(
            item ->
                item.setProductQuantity(
                    item.getProductQuantity() + cartItemRequest.getProductQuantity()),
            () ->
                cart.getCartItemList()
                    .add(
                        CartItem.builder()
                            .productName(product.getName())
                            .productId(product.getId())
                            .productPrice(product.getPrice())
                            .productQuantity(cartItemRequest.getProductQuantity())
                            .build()));
    saveCart(userId, cart);
    return cart;
  }

  public void deleteCart(UUID userId) {
    if (!hasCart(userId)) {
      throw new RuntimeException("Cart not found for user : " + userId.toString());
    }
    redisTemplate.delete(getCART_PREFIX(userId));
  }

  public void deleteItem(UUID userId, UUID productId) {
    if (!hasCart(userId)) {
      throw new RuntimeException("Cart not found for user : " + userId.toString());
    }
    Object object = redisTemplate.opsForValue().get(getCART_PREFIX(userId));
    Cart cart = checkObjectAndGetCart(object);
    cart.getCartItemList().stream()
        .filter(item -> item.getProductId().equals(productId))
        .findFirst()
        .ifPresent(
            item -> {
              cart.getCartItemList().remove(item);
            });
    saveCart(userId, cart);
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
    saveCart(userId, cart);
    return cart;
  }

  private void saveCart(UUID userId, Cart cart) {
    redisTemplate.opsForValue().set(getCART_PREFIX(userId), cart, duration);
  }

  private Cart checkObjectAndGetCart(Object object) {
    if (!(object instanceof Cart cart)) {
      throw new RuntimeException("Cart object is not instance of Cart.");
    }
    return cart;
  }
}
