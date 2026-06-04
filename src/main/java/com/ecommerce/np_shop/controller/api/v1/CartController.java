package com.ecommerce.np_shop.controller.api.v1;

import com.ecommerce.np_shop.dto.api.v1.CartItemRequest;
import com.ecommerce.np_shop.security.AccountDetails;
import com.ecommerce.np_shop.service.CartService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${base.api}")
@RequiredArgsConstructor
public class CartController {
  private final CartService cartService;

  @GetMapping("/cart")
  public ResponseEntity<?> getCart(Authentication authentication) {
    verifyAuthenticationObject(authentication);
    return ResponseEntity.ok(cartService.getCart(getId(authentication)));
  }

  @PostMapping("/cart")
  public ResponseEntity<?> addItemToCart(
      Authentication authentication, @Valid @RequestBody CartItemRequest cartItemRequest) {
    verifyAuthenticationObject(authentication);
    return ResponseEntity.ok(cartService.addItem(getId(authentication), cartItemRequest));
  }

  @DeleteMapping("/cart")
  public ResponseEntity<?> removeCart(Authentication authentication) {
    verifyAuthenticationObject(authentication);
    cartService.deleteCart(getId(authentication));
    return ResponseEntity.ok("Cart has been deleted");
  }

  @DeleteMapping("/cart/{id}")
  public ResponseEntity<?> removeItemFromCart(
      Authentication authentication, @PathVariable(name = "id") UUID id) {
    verifyAuthenticationObject(authentication);
    cartService.deleteItem(getId(authentication), id);
    return ResponseEntity.ok("CartItem has been deleted");
  }

  private void verifyAuthenticationObject(Authentication authentication) {
    if (!(authentication.getPrincipal() instanceof AccountDetails)) {
      throw new RuntimeException("Authentication principal is not AccountDetails");
    }
  }

  private UUID getId(Authentication authentication) {
    return ((AccountDetails) authentication.getPrincipal()).getId();
  }
}
