package com.ecommerce.np_shop.payment.paypal.service;

import com.ecommerce.np_shop.entity.Order;
import com.ecommerce.np_shop.entity.Product;
import com.ecommerce.np_shop.enums.OrderStatus;
import com.ecommerce.np_shop.enums.PaymentStatus;
import com.ecommerce.np_shop.repo.OrderRepository;
import com.ecommerce.np_shop.repo.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.models.GetOrderInput;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
@Transactional
public class PaypalWebhookService {
  private final ProductRepository productRepository;
  private final OrderRepository orderRepository;
  private final PayPalAuthService payPalAuthService;
  private final PaypalServerSdkClient paypalServerSdkClient;
  private final PayPalService payPalService;
  private final ObjectMapper objectMapper = new  ObjectMapper();
  private final HttpClient httpClient = HttpClient.newHttpClient();
  @Value("${paypal.webhook.id}")
  private String webhookId;
  public void handleCaptureComplete(String paypalId) {
      Order order = orderRepository.findByPaymentPaymentId(paypalId);
    if (order == null) {
      throw new RuntimeException("Payment not found hcc");
    }
    if (!PaymentStatus.PAID.toString().equals(order.getPayment().getStatus())) {
        /// Maybe the webhook already handle the capture before the capture has bent sent by the frontend
        if (!"COMPLETED".equalsIgnoreCase(getOrderStatus(paypalId))) {
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
  }

  public void handleCaptureFailed(String paypalId) {
      System.out.println("from failed wb");
    Order order = orderRepository.findByPaymentPaymentId(paypalId);
    if (order == null) {
      throw new RuntimeException("Payment not found hcf");
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

  public boolean validWebhook(
      String transmissionId,
      String transmissionTime,
      String certUrl,
      String authAlgo,
      String transmissionSig,
      String rawBody)  {
      try{
      JsonNode webhook_event = objectMapper.readTree(rawBody);
          ObjectNode payload = objectMapper.createObjectNode();
          payload.put("transmission_id", transmissionId);
          payload.put("transmission_time", transmissionTime);
          payload.put("cert_url", certUrl);
          payload.put("auth_algo", authAlgo);
          payload.put("transmission_sig", transmissionSig);
          payload.put("webhook_id", webhookId);
          payload.set("webhook_event", webhook_event);
          String payloadString = objectMapper.writeValueAsString(payload);
          HttpRequest request = HttpRequest.newBuilder()
                  .uri(URI.create(payPalAuthService.getBaseUrl()+"/v1/notifications/verify-webhook-signature"))
                  .header("Authorization" , "Bearer " + payPalAuthService.getAccessToken())
                  .header("Content-Type" , "application/json")
                  .POST(HttpRequest.BodyPublishers.ofString(payloadString))
                  .build();
          HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
          JsonNode response_event = objectMapper.readTree(response.body());
          String status = response_event.path("verification_status").asText(null);
          return "SUCCESS".equals(status);
      }catch(JsonProcessingException e){
          throw  new RuntimeException(String.format("Invalid webhook event : [error-message : %s]",e.getMessage()));
      } catch (IOException | InterruptedException e) {
          throw new RuntimeException(String.format("Failed to send request to PayPal : [error-message : %s]",e.getMessage()));
      }
  }


  public String getPayPalOrderStatus(String paypalId){
      var order = getPayPalOrder(paypalId);
      return order.getStatus().toString();
  }

  private com.paypal.sdk.models.Order getPayPalOrder(String paypalId) {
      try{
      GetOrderInput getOrderInput = new GetOrderInput.Builder(paypalId).build();
     return paypalServerSdkClient.getOrdersController().getOrder(getOrderInput).getResult();
              }catch (ApiException | IOException apiException){
          throw new RuntimeException(String.format("Failed to get PayPal Order: [paypal id = %s , error-message = %s]" , paypalId , apiException.getMessage()));
      }
  }

  public void handleApprove(String paypalId) {
      Order order = orderRepository.findByPaymentPaymentId(paypalId);
      if (order == null) {
          throw new RuntimeException("Payment not found hp");
      }
      String status = getPayPalOrderStatus(paypalId);
      if (PaymentStatus.PAID.toString().equals(order.getPayment().getStatus())) {
          if("COMPLETED".equals(status)) {
              return;
          }
      }
      if ("APPROVED".equals(status)){
          payPalService.capturePayment(paypalId);
      }
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
