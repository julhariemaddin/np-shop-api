package com.ecommerce.np_shop.payment.paypal.service;

import com.ecommerce.np_shop.entity.Order;
import com.ecommerce.np_shop.entity.Product;
import com.ecommerce.np_shop.enums.OrderStatus;
import com.ecommerce.np_shop.enums.PaymentStatus;
import com.ecommerce.np_shop.repo.OrderRepository;
import com.ecommerce.np_shop.repo.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaypalWebhookService {
  private final ProductRepository productRepository;
  private final OrderRepository orderRepository;

  @Transactional
  public void handleCaptureComplete(String paypalId) {
    Order order = orderRepository.findByPaymentPaymentId(paypalId);
    if (order == null) {
      throw new RuntimeException("Payment not found");
    }
    if (!PaymentStatus.PAID.toString().equals(order.getPayment().getStatus())) {
      order.setStatus(OrderStatus.CONFIRMED.toString());
      order
          .getOrderItems()
          .forEach(
              item -> {
                Product product = productRepository.getById(item.getProductId());
                product.setStock(product.getStock() - item.getQuantity());
                product.setReserveStock(product.getReserveStock() - item.getQuantity());
                productRepository.save(product);
              });
      order.getPayment().setStatus(PaymentStatus.PAID.toString());
      orderRepository.save(order);
    }
  }

  @Transactional
  public void handleCaptureFailed(String paypalId) {
    Order order = orderRepository.findByPaymentPaymentId(paypalId);
    if (order == null) {
      throw new RuntimeException("Payment not found");
    }
    order.setStatus(OrderStatus.CONFIRMED.toString());
    order
        .getOrderItems()
        .forEach(
            item -> {
              Product product = productRepository.getById(item.getProductId());
              product.setReserveStock(product.getReserveStock() - item.getQuantity());
              productRepository.save(product);
            });
    order.getPayment().setStatus(PaymentStatus.FAILED.toString());
    orderRepository.save(order);
  }
}
