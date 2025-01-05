package com.example.userManagementService.models;

import com.example.userManagementService.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OtpUtil {

    public String generateOtp() {
        String forgetPasswordCode = UUID.randomUUID().toString().substring(0, 6);
        return forgetPasswordCode;
    }

}
