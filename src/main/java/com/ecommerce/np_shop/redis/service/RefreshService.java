package com.ecommerce.np_shop.redis.service;

import com.ecommerce.np_shop.dto.api.refresh.RefreshResponse;

import java.util.UUID;

public interface RefreshService {
     RefreshResponse getAccessToken(String refreshToken);
     String getRefreshToken(UUID accountId ,String accessToken);
}
