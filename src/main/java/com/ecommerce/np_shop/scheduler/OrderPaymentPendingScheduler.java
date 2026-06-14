package com.ecommerce.np_shop.scheduler;

import com.ecommerce.np_shop.entity.Order;
import com.ecommerce.np_shop.enums.OrderStatus;
import com.ecommerce.np_shop.enums.PaymentStatus;
import com.ecommerce.np_shop.repo.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class OrderPaymentPendingScheduler {
    @Autowired
    private OrderRepository orderRepository;
    @Scheduled(fixedDelay = 60000)
    public void pollingPendingPayments(){
        List<Order> orders = orderRepository.findByStatusAndExpiredAtBefore(OrderStatus.PENDING_PAYMENT.toString(), Instant.now());
        for (Order order : orders){
            order.setStatus(OrderStatus.PAYMENT_FAILED.toString());
            order.getPayment().setStatus(PaymentStatus.TIMEOUT.toString());
            orderRepository.save(order);
        }
    }
}
