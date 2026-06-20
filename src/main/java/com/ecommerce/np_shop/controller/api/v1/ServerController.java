package com.ecommerce.np_shop.controller.api.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/server")
public class ServerController {
    @GetMapping("Check")
    public ResponseEntity<?> check() {
        return ResponseEntity.ok("Server checked");
    }
}
