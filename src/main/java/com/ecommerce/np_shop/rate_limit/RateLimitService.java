package com.ecommerce.np_shop.rate_limit;

import com.ecommerce.np_shop.enums.RateLimiterKey;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RateLimitService {
  private final RedissonClient redissonClient;
  private final Map<String, EndpointLimit> ProtectedEndpointLimits =
      Map.of(
          "/api/auth/sign-in",
          new EndpointLimit(5, 1, RateIntervalUnit.MINUTES),
          "/api/auth/register",
          new EndpointLimit(10, 1, RateIntervalUnit.MINUTES));

  public boolean allowedRequest(UUID accountId, String IP, String path) {
    if (accountId != null) {
      if (!checkLimit(
          RateLimiterKey.ACCOUNT.getValue() + accountId.toString(),
          50,
          1,
          RateIntervalUnit.MINUTES)) {
        return false;
      }
      if (checkUserRoutes(path)) {
        EndpointLimit endpointLimit = ProtectedEndpointLimits.get(getRoot(path));
        return checkLimit(
            RateLimiterKey.ENDPOINT.getValue() + accountId.toString(),
            endpointLimit.rate,
            endpointLimit.interval,
            endpointLimit.unit);
      }
      return true;
    }

    if (!checkLimit(RateLimiterKey.IP.getValue() + IP, 10, 1, RateIntervalUnit.MINUTES)) {
      return false;
    }
    if (checkUserRoutes(path)) {
      if (path.equals("/api/auth/sign-in")) {
        EndpointLimit endpointLimit = ProtectedEndpointLimits.get(getRoot(path));
        return checkLimit(
            RateLimiterKey.ENDPOINT.getValue() + IP,
            endpointLimit.rate,
            endpointLimit.interval,
            endpointLimit.unit);
      }
    }
    return true;
  }

  public String getRoot(String path) {
    for (String key : ProtectedEndpointLimits.keySet()) {
      if (key.startsWith(path + "/") || key.equals(path)) {
        return key;
      }
    }
    return path;
  }

  public boolean checkUserRoutes(String path) {
    for (String key : ProtectedEndpointLimits.keySet()) {
      if (path.equals(key) || path.startsWith(key + "/")) {
        return true;
      }
    }
    return false;
  }

  private boolean checkLimit(String key, int rate, int interval, RateIntervalUnit unit) {
    RRateLimiter limiter = redissonClient.getRateLimiter(key);
    limiter.trySetRate(RateType.OVERALL, rate, interval, unit);
    return limiter.tryAcquire(1);
  }

  private record EndpointLimit(int rate, int interval, RateIntervalUnit unit) {}
  ;
}
