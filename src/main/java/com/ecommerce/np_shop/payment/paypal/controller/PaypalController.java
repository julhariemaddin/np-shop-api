package com.ecommerce.np_shop.payment.paypal.controller;


import com.ecommerce.np_shop.payment.dto.PaymentRequest;
import com.ecommerce.np_shop.payment.dto.PaymentResponse;
import com.ecommerce.np_shop.payment.paypal.service.PayPalService;
import com.ecommerce.np_shop.security.AccountDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paypal")
@RequiredArgsConstructor
public class PaypalController {
    private final PayPalService paymentService;
    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest paymentRequest , Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof AccountDetails)) {
            throw new RuntimeException("Account details are wrong");
        }
        return ResponseEntity.ok(paymentService.payment(paymentRequest,((AccountDetails) authentication.getPrincipal()).getId()));
    }

    @GetMapping("/capture")
    public ResponseEntity<?> success(@RequestParam("token") String paypalId) {
        return ResponseEntity.ok(paymentService.capturePayment(paypalId));
    }
    @GetMapping("/cancel")
    public ResponseEntity<?> failed(@RequestParam("token" ) String paypalId) {
        System.out.println("From Cancel paypalId: " + paypalId);
        return ResponseEntity.ok(paymentService.cancelPayment(paypalId));
    }
}
