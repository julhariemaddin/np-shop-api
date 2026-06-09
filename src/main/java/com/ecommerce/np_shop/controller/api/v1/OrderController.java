package com.ecommerce.np_shop.controller.api.v1;

import com.ecommerce.np_shop.dto.api.v1.OrderResponse;
import com.ecommerce.np_shop.security.AccountDetails;
import com.ecommerce.np_shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${base.api}")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PostMapping("/order")
    public ResponseEntity<?> createOrder(Authentication authentication){
        return ResponseEntity.ok(orderService.createOrder(checkAccountAndGetId(authentication)));
    }
    @GetMapping("/order")
    public ResponseEntity<List<OrderResponse>> getOrders(Authentication authentication){
        return ResponseEntity.ok().body(orderService.getOrders(checkAccountAndGetId(authentication)));
    }
    @GetMapping("/order/{id}")
    public ResponseEntity<?> getOrder(Authentication authentication, @PathVariable(name = "id") UUID orderId){
        return ResponseEntity.ok().body(orderService.getOrder(checkAccountAndGetId(authentication),orderId));
    }
    @DeleteMapping("/order/{id}")
    public ResponseEntity<?> deleteOrder(Authentication authentication, @PathVariable(name = "id") UUID orderId){
        orderService.deleteOrder(checkAccountAndGetId(authentication),orderId);
        return ResponseEntity.ok("Successfully deleted order");
    }

    public UUID checkAccountAndGetId(Authentication authentication){
        if(!(authentication.getPrincipal() instanceof AccountDetails accountDetails)){
            throw new RuntimeException("Not authorized");
        }
        return accountDetails.getId();
    }
}
