package com.ecommerce.np_shop.service.serviceImpl;

import com.ecommerce.np_shop.entity.Order;
import com.ecommerce.np_shop.entity.Payment;
import com.ecommerce.np_shop.repo.PaymentRepository;
import com.ecommerce.np_shop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
  private final PaymentRepository paymentRepository;

  @Override
  @PreAuthorize("hasRole('USER')")
  public Payment createPayment(Order order) {
    Payment payment = new Payment();
    payment.setOrder(order);
    payment.setTotalPrice(order.getTotalPrice());
    payment.setStatus("Paid");
    return paymentRepository.save(payment);
  }
}
