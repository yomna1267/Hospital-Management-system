package com.example.userManagementService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final EmailService emailService;
    private final PasswordEncoder encoder;

    public void redisSaveForgetPasswordCode(String key , String value ){
        Jedis jedis = new Jedis("localhost",6379);
        jedis.setex(key,120, encoder.encode(value));
        System.out.println(jedis.ttl(key));
//        jedis.close();
    }
}
