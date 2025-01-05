package com.example.userManagementService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    private static final int MAX_REQUESTS = 1;
    private static final int MAX_REQUESTS_DURATION = 172800000;

    public boolean isRateLimited(String username) {
        String key = "change_password_times:" + username;

        String attempts = redisTemplate.opsForValue().get(key);
        if (attempts != null && Integer.parseInt(attempts) >= MAX_REQUESTS) {
            return true;
        }

        redisTemplate.opsForValue().increment(key, 1);

        if (attempts == null) {
            redisTemplate.expire(key, Duration.ofSeconds(MAX_REQUESTS_DURATION));
        }

        return false;
    }
}
