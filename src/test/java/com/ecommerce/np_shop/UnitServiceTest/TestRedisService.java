package com.ecommerce.np_shop.UnitServiceTest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import static org.junit.jupiter.api.Assertions.assertEquals;

@Service
@Slf4j
public class TestRedisService {
     public void testRedis(RedisTemplate<String,Object> redisTemplate){
         log.info("Phase 1");
        redisTemplate.opsForValue().set("il","lol");
        assertEquals("lol",redisTemplate.opsForValue().get("il"));
    }
}
