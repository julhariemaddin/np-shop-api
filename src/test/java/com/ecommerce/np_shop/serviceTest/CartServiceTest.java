package com.ecommerce.np_shop.serviceTest;


import com.ecommerce.np_shop.dto.api.v1.Cart;
import com.ecommerce.np_shop.dto.api.v1.CartItem;
import com.ecommerce.np_shop.dto.api.v1.CartItemRequest;
import com.ecommerce.np_shop.entity.Category;
import com.ecommerce.np_shop.entity.Product;
import com.ecommerce.np_shop.service.serviceImpl.CartServiceImpl;
import com.ecommerce.np_shop.service.serviceImpl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@Service
@SpringBootTest
public class CartServiceTest {
    //Mock
    @Mock
    private RedisTemplate<String,Object> redisTemplate;
    @Mock
    private ValueOperations<String,Object> valueOperations;
    @Mock
    private ProductServiceImpl productService;
    //InjectMock
    @InjectMocks
    private CartServiceImpl cartService;
    //Needs
    private UUID userId;
    private Cart cart;
    private final String CART_PREFIX = "cart:";
    private Product product,product2;
    private CartItemRequest cartItemRequest , cartItemRequest2;


    @BeforeEach
    public void setup() {

        userId = UUID.randomUUID();

        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setCategoryName("test");

        product = new Product();
        product.setCategory(category);
        product.setId(UUID.randomUUID());
        product.setName("test");
        product.setStock(2);
        product.setDescription("test");
        product.setPrice(900);
        product.setImageUrl("test");

        product2 = new Product();
        product2.setCategory(category);
        product2.setId(UUID.randomUUID());
        product2.setName("test");
        product2.setStock(2);
        product2.setDescription("test");
        product2.setPrice(900);
        product2.setImageUrl("test");


        CartItem item = CartItem.builder()
                .productName(product.getName())
                .productId(product.getId())
                .productPrice(product.getPrice())
                .productQuantity(2)
                .build();

        cartItemRequest  = new CartItemRequest();
        cartItemRequest.setProductId(product.getId());
        cartItemRequest.setProductQuantity(3);

        cartItemRequest2  = new CartItemRequest();
        cartItemRequest2.setProductId(product2.getId());
        cartItemRequest2.setProductQuantity(3);

        cart = new Cart();
        cart.getCartItemList().add(item);
        cart.setUserId(userId);
    }
    @Test
    void getCart_ShouldReturnCart_WhenCartExists() {
        when(redisTemplate.hasKey(CART_PREFIX + userId.toString())).thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(CART_PREFIX + userId)).thenReturn(cart);

        Cart result = cartService.getCart(userId);

        assertNotNull(result);
        assertEquals(result.getUserId(), userId);
        assertEquals(1, result.getCartItemList().size());
    }

    @Test
    void getCart_ShouldReturnNewCart_WhenCartDoesNotExists() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(CART_PREFIX + userId)).thenReturn(null);

        Cart result = cartService.getCart(userId);

        assertNotNull(result);
        assertEquals(result.getUserId(), userId);
        assertEquals(0, result.getCartItemList().size());
    }

    @Test
    void addItem_ShouldAddItemToCart_WhenCartExists() {
        when(redisTemplate.hasKey(CART_PREFIX + userId.toString())).thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(CART_PREFIX + userId)).thenReturn(cart);
        when(productService.checkProductExistsAndGetProduct(cartItemRequest2.getProductId())).thenReturn(product2);

        Cart result = cartService.addItem(userId, cartItemRequest2);
        assertNotNull(result);
        assertEquals(result.getUserId(), userId);
        assertEquals(2 , result.getCartItemList().size());
    }

    @Test
    void addItem_ShouldAddItemToCart_WhenCartDoesNotExists() {
        when(redisTemplate.hasKey(CART_PREFIX + userId.toString())).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(CART_PREFIX + userId)).thenReturn(null);
        when(productService.checkProductExistsAndGetProduct(cartItemRequest.getProductId())).thenReturn(product2);
        Cart result = cartService.addItem(userId, cartItemRequest);
        assertNotNull(result);
        assertEquals(result.getUserId(), userId);
        assertEquals(1 , result.getCartItemList().size());
    }

    @Test
    void addItem_ShouldThrowException_WhenRedisValueIsNotCartInstance(){
        when(redisTemplate.hasKey(CART_PREFIX + userId.toString())).thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(CART_PREFIX + userId)).thenReturn(null);
        when(productService.checkProductExistsAndGetProduct(cartItemRequest.getProductId())).thenReturn(product2);

        assertThrows(RuntimeException.class, () -> cartService.addItem(userId, cartItemRequest));
    }

    @Test
    void addItem_ShouldIncrementsQuantity_WhenTheresAnExistingCartItem() {
        when(redisTemplate.hasKey(CART_PREFIX + userId.toString())).thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(CART_PREFIX + userId)).thenReturn(cart);
        when(productService.checkProductExistsAndGetProduct(cartItemRequest.getProductId())).thenReturn(product);

        Cart result = cartService.addItem(userId, cartItemRequest);
        CartItem testCart;
        result.getCartItemList().stream().filter(
                item -> item.getProductId().equals(cartItemRequest.getProductId())
        ).findFirst().ifPresent(item -> {assertEquals(5,item.getProductQuantity());});


    }

}
