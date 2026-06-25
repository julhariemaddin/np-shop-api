package com.ecommerce.np_shop.service.serviceImpl;

import com.ecommerce.np_shop.dto.api.v1.*;
import com.ecommerce.np_shop.entity.Order;
import com.ecommerce.np_shop.entity.OrderItem;
import com.ecommerce.np_shop.enums.OrderStatus;
import com.ecommerce.np_shop.redis.model.Cart;
import com.ecommerce.np_shop.redis.model.CartItem;
import com.ecommerce.np_shop.redis.service.CartService;
import com.ecommerce.np_shop.repo.AccountRepository;
import com.ecommerce.np_shop.repo.OrderItemRepository;
import com.ecommerce.np_shop.repo.OrderRepository;
import com.ecommerce.np_shop.repo.ProductRepository;
import com.ecommerce.np_shop.service.OrderService;
import com.ecommerce.np_shop.service.PaymentService;
import com.ecommerce.np_shop.service.ProductService;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
  private final OrderItemRepository orderItemRepository;
  private final CartService cartService;
  private final OrderRepository orderRepository;
  private final PaymentService paymentService;
  private final AccountRepository accountRepository;
  private final ProductService productService;

  @PreAuthorize("hasRole('USER')")
  @Transactional
  public OrderResponse createOrder(UUID userId) {
    Order order = orderRepository.save(new Order());
    Cart cart = cartService.getCart(userId);
    order.setAccount(
        accountRepository
            .findById(userId)
            .orElseThrow(() -> new RuntimeException("No account found!")));
    order.setTotalItemsQuantity(0);
    List<OrderItem> orderItems = new ArrayList<>();
    if (cart.getCartItemList().isEmpty()) {
      throw new RuntimeException("Cart is empty");
    }
    List<CartItem> cartItemList = cart.getCartItemList();
    for (CartItem cartItem : cartItemList) {
      OrderItem orderItem = new OrderItem();
      orderItem.setOrder(order);
      orderItem.setProductId(cartItem.getProductId());
      orderItem.setProductStatus(true);
      orderItem.setProductName(cartItem.getProductName());
      orderItem.setPrice(cartItem.getProductPrice());
      orderItem.setQuantity(cartItem.getProductQuantity());
      order.setTotalItemsQuantity(order.getTotalItemsQuantity() + orderItem.getQuantity());
      OrderItem saveOrderItem = orderItemRepository.save(orderItem);
      orderItems.add(saveOrderItem);
    }
    order.setOrderItems(orderItems);
    order.setPayment(paymentService.createPayment(order));
    order.setStatus(OrderStatus.PENDING_PAYMENT.toString());
    order.setExpiredAt(Instant.now().plus(Duration.ofSeconds(1)));
    Order saveOrder =  orderRepository.save(order);
    cartService.deleteCart(saveOrder.getAccount().getId());
    return getOrderResponse(saveOrder);
  }

    @Override
    @PreAuthorize("hasRole('USER')")
    public Page<OrderResponse> getOrders(UUID userId , Pageable pageable) {
        return orderRepository.findByAccountId(userId,pageable).map(
                this::getOrderResponse
        );
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public OrderResponse getOrder(UUID userId , UUID orderId) {
       Order order = orderRepository.findByIdAndAccountId(orderId,userId).orElseThrow(() -> new RuntimeException("No order found!"));
       return getOrderResponse(order);
    }



    @Override
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public void deleteOrder(UUID userId, UUID orderId) {
    Order order = orderRepository.findByIdAndAccountId(orderId,userId).orElse(null);
      if (order != null) {

          orderRepository.deleteById(orderId);
          return;
      }
      throw new RuntimeException("No order found!");
    }

    private OrderResponse getOrderResponse(Order order) {
      return OrderResponse.builder()
              .orderId(order.getId())
              .status(order.getStatus())
              .orderItems(
                      order.getOrderItems().stream()
                              .map(
                                      item ->
                                              OrderItemRespond.builder()
                                                      .id(item.getId())
                                                      .orderId(item.getOrder().getId())

                                                      .price(item.getPrice())
                                                      .quantity(item.getQuantity())
                                                      .productStatus(productService.checkProductStatus(item.getProductId()))
                                                      .productName(item.getProductName())
                                                      .productId(item.getProductId())
                                                      .build())
                              .toList())
              .accountId(order.getAccount().getId())
              .payment(getPaymentResponse(order))
              .totalItemsQuantity(order.getTotalItemsQuantity())
              .totalPrice(order.getTotalPrice())
              .createdAt(order.getCreatedAt())
              .build();
    }
    private PaymentRespond getPaymentResponse(Order order) {
    return PaymentRespond.builder()
        .orderId(order.getId())
        .status(order.getPayment().getStatus())
        .id(order.getPayment().getId())
        .build();
    }
}
