package com.ecommerce.np_shop.payment.paypal.controller;


import com.ecommerce.np_shop.payment.paypal.service.PaypalWebhookService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class PaypalWebhook {
    private final PaypalWebhookService paypalWebhookService;
    @PostMapping("/paypal")
    public ResponseEntity<Void> paypalWebhook(@RequestBody JsonNode payload) {
        String eventType =  payload.get("event_type").asText();
        System.out.println(payload);
        switch (eventType) {
            case "PAYMENT.CAPTURE.COMPLETED":
                paypalWebhookService.handleCaptureComplete(getPaypalId(payload));
                break;
            case "PAYMENT.CAPTURE.DENIED", "PAYMENT.CAPTURE.DECLINED":
                paypalWebhookService.handleCaptureFailed(getPaypalId(payload));
                break;
        }
        return ResponseEntity.ok().build();
    }
    private String getPaypalId(JsonNode payload) {
        return payload.path("resource")
                .path("supplementary_data")
                .path("related_ids")
                .path("order_id")
                .asText();
    }
}
