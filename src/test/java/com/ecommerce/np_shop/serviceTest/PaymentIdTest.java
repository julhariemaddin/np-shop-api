package com.ecommerce.np_shop.serviceTest;

import com.ecommerce.np_shop.entity.Order;
import com.ecommerce.np_shop.repo.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Service
@Transactional
public class PaymentIdTest {
    @Autowired
    private OrderRepository orderRepository;
    @Test
    public void paymentIdTest(){
        String orderId = "9XE73469P8839442P";
        Order order = orderRepository.findByPaymentPaymentId(orderId);
        System.out.println(order.getPayment().getPaymentId());
        assertNotNull(order);
    }
}
