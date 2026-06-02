package com.ecommerce.np_shop.dto.api.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
