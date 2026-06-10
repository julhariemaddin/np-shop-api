package com.ecommerce.np_shop.controller.api.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    //TODO continue this integration
    @PatchMapping("/me")
    public ResponseEntity<?> updateMe(){
        return ResponseEntity.ok().build();
    }
}
