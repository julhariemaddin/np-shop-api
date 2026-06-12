package com.ecommerce.np_shop.dto.api.refresh;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class RefreshResponse {
    private UUID accountId;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime refreshTokenExpireTime;
    private LocalDateTime accessTokenExpireTime;
}
