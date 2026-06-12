package com.ecommerce.np_shop.redis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Refresh implements Serializable {
    private UUID accountId;
    private String accessToken;
    private String refreshToken;
    private Instant refreshTokenExpirationTime;
    private Instant accessTokenExpirationTime;
}
