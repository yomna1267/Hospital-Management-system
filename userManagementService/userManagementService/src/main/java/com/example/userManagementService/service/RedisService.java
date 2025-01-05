package com.example.userManagementService.service;

import com.example.userManagementService.exceptions.OtpOrTokenExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final PasswordEncoder encoder;
    private final long OTP_TTL = 180 ;
    private final StringRedisTemplate redisTemplate;



    public void redisSaveForgetPasswordCode(String key , String value ){
        redisTemplate.opsForValue().set(key,encoder.encode(value), Duration.ofSeconds(OTP_TTL));
    }

    public String getOtp(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void validateOtp(String username, String otpCode) {
        String redisStoredOtp =getOtp(username);
        if(redisStoredOtp == null )
            throw new OtpOrTokenExpiredException("Expired OTP");

        if (!redisStoredOtp.equals(encoder.encode(otpCode))) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        long ttl = getTimeToLive(username);
        if (ttl <= 0) {
            throw new IllegalArgumentException("OTP Expired");
        }
    }

    public long getTimeToLive(String key){
        return redisTemplate.getExpire(key);
    }
}