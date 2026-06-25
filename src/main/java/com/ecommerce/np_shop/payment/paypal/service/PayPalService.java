package com.ecommerce.np_shop.payment.paypal.service;

import com.ecommerce.np_shop.entity.Order;
import com.ecommerce.np_shop.entity.OrderItem;
import com.ecommerce.np_shop.entity.Product;
import com.ecommerce.np_shop.enums.OrderStatus;
import com.ecommerce.np_shop.enums.PaymentStatus;
import com.ecommerce.np_shop.enums.RLockKey;
import com.ecommerce.np_shop.payment.dto.PaymentRequest;
import com.ecommerce.np_shop.payment.dto.PaymentResponse;
import com.ecommerce.np_shop.repo.OrderRepository;
import com.ecommerce.np_shop.repo.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.models.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PayPalService {
  private final OrderRepository orderRepository;
  private final PaypalServerSdkClient paypalServerSdkClient;
  private final ProductRepository productRepository;
  private final PayPalAuthService payPalAuthService;
  private final HttpClient httpClient = HttpClient.newHttpClient();
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final RedissonClient redissonClient;

  @Value("${paypal.return-url}")
  private String returnUrl;

  @Value("${paypal.cancel-url}")
  private String cancelUrl;


  public PaymentResponse payment(PaymentRequest paymentRequest, UUID userId) {

      Order order =
          orderRepository
              .findByIdAndAccountId(UUID.fromString(paymentRequest.getOrderId()), userId)
              .orElseThrow(() -> new RuntimeException("Order not found"));
      RLock orderLock = redissonClient.getLock(RLockKey.ORDER.getValue()+order.getId());
    try {
      if(!orderLock.tryLock(5,10,TimeUnit.SECONDS)){
        throw new RuntimeException("Order lock timed out");
      }
      if (order.getPayment().getStatus().equals(PaymentStatus.PAID.toString())) {
        throw new RuntimeException("Payment status is already paid");
      }
      if (order.getPayment().getStatus().equals(PaymentStatus.PROCESSING.toString())) {
        throw new RuntimeException("Payment status is already processing");
      }
      String totalAmount =
          BigDecimal.valueOf(order.getTotalPrice())
              .setScale(2, RoundingMode.HALF_UP)
              .toPlainString();

      AmountWithBreakdown amount = new AmountWithBreakdown.Builder("PHP", totalAmount).build();

      PurchaseUnitRequest purchaseUnitRequest =
          new PurchaseUnitRequest.Builder(amount).referenceId(order.getId().toString()).build();

      OrderApplicationContext orderApplicationContext =
          new OrderApplicationContext.Builder().returnUrl(returnUrl).cancelUrl(cancelUrl).build();

      OrderRequest orderRequest =
          new OrderRequest.Builder(CheckoutPaymentIntent.CAPTURE, List.of(purchaseUnitRequest))
              .applicationContext(orderApplicationContext)
              .build();

      CreateOrderInput input = new CreateOrderInput.Builder(null, orderRequest).build();

      com.paypal.sdk.models.Order paypalOrder =
          paypalServerSdkClient.getOrdersController().createOrder(input).getResult();

      String approvalUrl =
          paypalOrder.getLinks().stream()
              .filter(link -> "approve".equals(link.getRel()))
              .findFirst()
              .map(LinkDescription::getHref)
              .orElseThrow(() -> new RuntimeException("Order approval url not found"));

      order.getOrderItems().forEach(orderItem -> {
        Product product = productRepository.findById(orderItem.getProductId()).orElseThrow(() -> new RuntimeException("No product found!"));
        RLock productLock = redissonClient.getLock(RLockKey.PRODUCT.getValue()+product.getId());
        try{
          if(!productLock.tryLock(5,10,TimeUnit.SECONDS)){
            throw new RuntimeException("Product lock timed out");
          }
        if(product.getStock() <= 0 || (product.getStock() < orderItem.getQuantity())){
          throw new RuntimeException("Insufficient stock : " + product.getName() + " , Available Stock : " + product.getStock());
        }
        product.setReserveStock(product.getReserveStock() + orderItem.getQuantity());
        productRepository.save(product);
        }catch(InterruptedException e){
          throw new RuntimeException(e);
        }finally{
          if(productLock.isHeldByCurrentThread()){
            productLock.unlock();
          }
}
      });
      order.getPayment().setStatus(PaymentStatus.PROCESSING.toString());
      order.getPayment().setPaymentId(paypalOrder.getId());
      order.setStatus(OrderStatus.PAYMENT_PROCESSING.toString());
      orderRepository.save(order);
      return PaymentResponse.builder()
          .paymentId(paypalOrder.getId())
          .approvalUrl(approvalUrl)
          .paymentStatus(paypalOrder.getStatus().toString())
          .build();
    } catch (IOException | InterruptedException ioException) {
      throw new RuntimeException(ioException.getMessage());
    } catch (ApiException apiException) {
      throw new RuntimeException(apiException);
    } finally{
      if(orderLock.isHeldByCurrentThread()){
        orderLock.unlock();
      }
}
  }

  public PaymentResponse capturePayment(String paypalId) {
    String paymentId;
    String status;
    if("COMPLETED".equals(getOrderStatus(paypalId))){
      return PaymentResponse.builder().paymentId(paypalId).paymentStatus("COMPLETED").build();
    }
      Order order = orderRepository.findByPaymentPaymentId(paypalId);
    RLock orderLock = redissonClient.getLock(RLockKey.ORDER.getValue()+order.getId());
    try {
      if(!orderLock.tryLock(5,10,TimeUnit.SECONDS)) {
        throw new RuntimeException("Order lock timed out");
      }
      if (OrderStatus.PAYMENT_FAILED.toString().equals(order.getStatus())) {
        throw new IllegalStateException("Order already timed out — refusing late capture");
      }
      CaptureOrderInput captureInput = new CaptureOrderInput.Builder(paypalId, null).build();
      com.paypal.sdk.models.Order captureOrder =
          paypalServerSdkClient.getOrdersController().captureOrder(captureInput).getResult();
      status = captureOrder.getStatus().toString();
      paymentId = captureOrder.getId();
      if ("COMPLETED".equals(status)) {
         order = orderRepository.findByPaymentPaymentId(paypalId);
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
          Product product =
              productRepository
                  .findById(orderItem.getProductId())
                  .orElseThrow(() -> new RuntimeException("Product not found"));
          RLock productLock = redissonClient.getLock(RLockKey.PRODUCT.getValue()+product.getId());
          try{
            if(!productLock.tryLock(5,10,TimeUnit.SECONDS)) {
              throw new RuntimeException("Product lock timed out");
            }
          if (product.getReserveStock() < orderItem.getQuantity()) {
            throw new RuntimeException("Product Reserve is insufficient");
          }
          product.setReserveStock(product.getReserveStock() - orderItem.getQuantity());
          product.setStock(product.getStock() - orderItem.getQuantity());
          productRepository.save(product);
          }catch (InterruptedException e){
            throw new RuntimeException(e.getMessage());
          }finally{
            if(productLock.isHeldByCurrentThread()){
              productLock.unlock();
            }
}
        }
        order.getPayment().setStatus(PaymentStatus.PAID.toString());
        order.setStatus(OrderStatus.CONFIRMED.toString());
        orderRepository.save(order);
      }
    } catch (ApiException | IOException | InterruptedException e) {
      throw new RuntimeException(e.getMessage());
   }finally{
      if(orderLock.isHeldByCurrentThread()){
        orderLock.unlock();
      }
}
    return PaymentResponse.builder().paymentId(paymentId).paymentStatus(status).build();
  }

  public PaymentResponse cancelPayment(String paypalId) {
    Order order = orderRepository.findByPaymentPaymentId(paypalId);
    /// Order done in cancel payment
    RLock orderLock = redissonClient.getLock(RLockKey.ORDER.getValue() + order.getId());
    try {
      boolean orderLocked = orderLock.tryLock(5, 10, TimeUnit.SECONDS);
      if(!orderLocked){
        throw new RuntimeException("Order Lock is not acquired");
      }
    if(PaymentStatus.PAID.toString().equals(order.getPayment().getStatus())) {
      throw new RuntimeException("Payment is already paid");
    }
    if(PaymentStatus.CANCEL.toString().equals(order.getPayment().getStatus())) {
      throw new RuntimeException("Payment is already cancelled");
    }

    order.getPayment().setStatus(PaymentStatus.CANCEL.toString());
      order
          .getOrderItems()
          .forEach(
              orderItem ->
                  productRepository
                      .findById(orderItem.getProductId())
                      .ifPresent(
                          product -> {
                            RLock productLock =
                                redissonClient.getLock(
                                    RLockKey.PRODUCT.getValue() + product.getId());
                            try {
                              boolean productLocked =
                                  productLock.tryLock(5, 10, TimeUnit.SECONDS);
                              if (!productLocked) {
                                throw new RuntimeException("Product lock not acquired");
                              }
                              if (product.getReserveStock() < orderItem.getQuantity()) {
                                throw new RuntimeException("Product reserve error");
                              }
                              product.setReserveStock(
                                  product.getReserveStock() - orderItem.getQuantity());
                            } catch (InterruptedException e) {
                              throw new RuntimeException(e);
                            } finally {
                              if (productLock.isHeldByCurrentThread()) {
                                productLock.unlock();
                              }
                            }
                          }));
    order.setStatus(OrderStatus.CANCELLED.toString());
    orderRepository.save(order);
    } catch (InterruptedException e) {
      throw new RuntimeException(e.getMessage());
    } finally {
      if (orderLock.isHeldByCurrentThread()) {
        orderLock.unlock();
      }
    }
    return PaymentResponse.builder()
        .paymentId(paypalId)
        .paymentStatus(order.getPayment().getStatus())
        .build();
  }

  public String getOrderStatus(String paypalId) {
    try{
      HttpRequest request = HttpRequest.newBuilder()
              .uri(URI.create(payPalAuthService.getBaseUrl() + "/v2/checkout/orders/" + paypalId))
              .header("Authorization", "Bearer " + payPalAuthService.getAccessToken())
              .GET()
              .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      JsonNode responseBody = objectMapper.readTree(response.body());
      return responseBody.path("status").asText(null);
    }catch (Exception e){
      throw new RuntimeException(String.format("Failed to get Order Status : [error-message : %s]",e.getMessage()));
    }
  }
}
