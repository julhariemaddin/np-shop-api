package com.ecommerce.np_shop.payment.paypal.controller;


import com.ecommerce.np_shop.payment.dto.OrderResource;
import com.ecommerce.np_shop.payment.dto.PaypalWebhookEnvelope;
import com.ecommerce.np_shop.payment.paypal.service.PaypalWebhookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class PaypalWebhook {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PaypalWebhookService paypalWebhookService;
    @PostMapping("/paypal")
    public ResponseEntity<Void> paypalWebhook(
            @RequestHeader("PAYPAL-TRANSMISSION-ID") String transmissionId,
            @RequestHeader("PAYPAL-TRANSMISSION-TIME")  String transmissionTime,
            @RequestHeader("PAYPAL-CERT-URL")  String certUrl,
            @RequestHeader("PAYPAL-AUTH-ALGO" )  String authAlgo,
            @RequestHeader("PAYPAL-TRANSMISSION-SIG") String transmissionSig,
            @RequestBody String rawBody

    ) throws JsonProcessingException {
        if(!paypalWebhookService.validWebhook(
                transmissionId,
                transmissionTime,
                certUrl,
                authAlgo,
                transmissionSig,
                rawBody)){
            throw new RuntimeException("Invalid Webhook Payload");
        }
        PaypalWebhookEnvelope paypalWebhookEnvelope = objectMapper.readValue(rawBody, PaypalWebhookEnvelope.class);
        String eventType =  paypalWebhookEnvelope.getEventType();
        OrderResource resource =  objectMapper.convertValue(paypalWebhookEnvelope.getResource(), OrderResource.class);
        switch (eventType) {
            case "PAYMENT.CAPTURE.COMPLETED":
                paypalWebhookService.handleCaptureComplete(resource.getSupplementary_data().getRelated_ids().getOrder_id());
                break;
            case "PAYMENT.CAPTURE.DENIED", "PAYMENT.CAPTURE.DECLINED":
                paypalWebhookService.handleCaptureFailed(resource.getId());
                break;
            case "CHECKOUT.ORDER.APPROVED":
                paypalWebhookService.handleApprove(resource.getId());
                break;

        }
        return ResponseEntity.ok().build();
    }
}
