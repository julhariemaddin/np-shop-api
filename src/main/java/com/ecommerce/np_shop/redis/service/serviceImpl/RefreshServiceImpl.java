package com.ecommerce.np_shop.redis.service.serviceImpl;

import com.ecommerce.np_shop.dto.api.refresh.RefreshResponse;
import com.ecommerce.np_shop.redis.model.Refresh;
import com.ecommerce.np_shop.redis.service.RefreshService;
import com.ecommerce.np_shop.security.AccountDetailService;
import com.ecommerce.np_shop.security.AccountDetails;
import com.ecommerce.np_shop.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshServiceImpl implements RefreshService {
    private final RedisTemplate<String,Object> redisTemplate;
    private final AccountDetailService accountDetailService;
    private final JwtService jwtService;
    private final Duration refreshTokenExpireTime = Duration.ofDays(7);
    @Override
    public RefreshResponse getAccessToken(String refreshToken) {
        String key = getREFRESH_PREFIX(refreshToken);
        Refresh refresh = (Refresh) redisTemplate.opsForValue().get(key);
        if(refresh == null){
            throw new RuntimeException("No Refresh found");
        }
        if(!refresh.getRefreshToken().equals(refreshToken)){
            throw new RuntimeException("Refresh Token doesn't match");
        }
        if(refresh.getRefreshTokenExpirationTime().isBefore(Instant.now())){
            redisTemplate.delete(key);
            throw new RuntimeException("Refresh Token expired");
        }
        AccountDetails accountDetails = (AccountDetails) accountDetailService.loadUserById(refresh.getAccountId());
        refresh.setRefreshToken(UUID.randomUUID().toString());
        redisTemplate.delete(key);
        if(refresh.getAccessTokenExpirationTime().isBefore(Instant.now())){
                String accessToken = jwtService.generateToken(accountDetails);
                Instant accessTokenExpiration = jwtService.getExpiration(accessToken);
                refresh.setAccessTokenExpirationTime(accessTokenExpiration);
                refresh.setAccessToken(accessToken);
        }
        redisTemplate.opsForValue().set(getREFRESH_PREFIX(refresh.getRefreshToken()),refresh);
         return getRefreshResponse(accountDetails.getId(),refresh);
    }

    @Override
    public String getRefreshToken(UUID accountId , String accessToken) {
        Refresh refresh = new Refresh();
        AccountDetails accountDetails = (AccountDetails) accountDetailService.loadUserById(accountId);
        Instant accessTokenExpirationTime = jwtService.getExpiration(accessToken);
        refresh.setAccessTokenExpirationTime(accessTokenExpirationTime);
        refresh.setAccessToken(accessToken);
        refresh.setRefreshToken(UUID.randomUUID().toString());
        refresh.setRefreshTokenExpirationTime(Instant.now().plus(refreshTokenExpireTime));
        refresh.setAccountId(accountDetails.getId());
    redisTemplate.opsForValue().set(getREFRESH_PREFIX(refresh.getRefreshToken()), refresh);
        return refresh.getRefreshToken();
    }
    private String getREFRESH_PREFIX(String refreshToken) {
        final String REFRESH_PREFIX = "refresh:";
        return REFRESH_PREFIX + refreshToken;
    }

    private RefreshResponse getRefreshResponse(UUID accountId , Refresh refresh) {
        return new RefreshResponse(
                accountId,
                refresh.getAccessToken(),
                refresh.getRefreshToken(),
                LocalDateTime.ofInstant(refresh.getRefreshTokenExpirationTime(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(refresh.getAccessTokenExpirationTime(), ZoneId.systemDefault()));
    }
}
