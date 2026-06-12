package com.ecommerce.np_shop.dto.api.auth;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String refreshToken;
    private String username;
    private List<String> role;
    private String email;
}
