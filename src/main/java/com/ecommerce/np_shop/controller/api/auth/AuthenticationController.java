package com.ecommerce.np_shop.controller.api.auth;


import com.ecommerce.np_shop.dto.api.auth.LoginRequest;
import com.ecommerce.np_shop.dto.api.auth.RegisterRequest;
import com.ecommerce.np_shop.exception.customException.NpBadCredentialsException;
import com.ecommerce.np_shop.security.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<?> registerAccount(@Valid @RequestBody RegisterRequest registerRequest) {
        return  ResponseEntity.ok(authService.register(registerRequest));
    }


    @PostMapping("/sign-in")
    public ResponseEntity<?> signInAccount(@RequestBody LoginRequest loginRequest)throws NpBadCredentialsException {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

}
