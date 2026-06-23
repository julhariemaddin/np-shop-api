package com.ecommerce.np_shop;



import com.ecommerce.np_shop.UnitServiceTest.TestRedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;


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
