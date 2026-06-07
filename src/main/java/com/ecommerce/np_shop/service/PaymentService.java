package com.ecommerce.np_shop.service;

import com.ecommerce.np_shop.entity.Order;
import com.ecommerce.np_shop.entity.Payment;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {
     Payment createPayment(Order order);
}
