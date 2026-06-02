package com.ecommerce.np_shop;



import com.ecommerce.np_shop.serviceTest.TestRedisService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NpShopNpShopApplicationTests {
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private TestRedisService testRedisService;
    @Test
	void contextLoads() {
	}
	@Test
	void testRedis() {
		testRedisService.testRedis(redisTemplate);
	}

}
