package com.ecommerce.np_shop.scheduler;

import com.ecommerce.np_shop.entity.Order;
import com.ecommerce.np_shop.enums.OrderStatus;
import com.ecommerce.np_shop.enums.PaymentStatus;
import com.ecommerce.np_shop.payment.paypal.service.PayPalService;
import com.ecommerce.np_shop.repo.OrderRepository;
import com.ecommerce.np_shop.repo.ProductRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderPaymentPendingScheduler {
  private final ProductRepository productRepository;
  private final PayPalService payPalService;
    @Autowired
    private OrderRepository orderRepository;

  @Scheduled(fixedDelay = 300000)
  public void pollingPendingPayments() {
    List<Order> orders =
        orderRepository.findByStatusAndExpiredAtBefore(
            OrderStatus.PAYMENT_PROCESSING.toString(), Instant.now());
        for (Order order : orders){
      String status = payPalService.getOrderStatus(order.getPayment().getPaymentId());
      if (!"COMPLETED".equals(status) && !"APPROVED".equals(status)) {
        order
            .getOrderItems()
            .forEach(
                orderItem ->
                    productRepository
                        .findById(orderItem.getProductId())
                        .ifPresent(
                            product ->
                                product.setReserveStock(
                                    product.getReserveStock() - orderItem.getQuantity())));
      }
      order.setStatus(OrderStatus.PAYMENT_FAILED.toString());
      order.getPayment().setStatus(PaymentStatus.TIMEOUT.toString());
      orderRepository.save(order);
    }
    }
}
