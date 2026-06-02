package com.ecommerce.np_shop.dto.api.auth;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private UUID id;
    private String username;
    private String email;
}
