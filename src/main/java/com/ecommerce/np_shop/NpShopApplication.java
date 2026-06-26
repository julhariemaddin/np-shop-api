package com.ecommerce.np_shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class NpShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(NpShopApplication.class, args);
    }
}