package com.example.userManagementService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final EmailService emailService;

    public void redisSaveForgetPasswordCode(String key ){
        Jedis jedis = new Jedis("localhost",6379);
        String value =emailService.sendForgetPasswordCode(key);
        jedis.setex(key,120,value);
        System.out.println(jedis.ttl(key));
//        jedis.close();
    }
}
