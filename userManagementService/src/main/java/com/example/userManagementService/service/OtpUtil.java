package com.example.userManagementService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OtpUtil {

    public String generateOtp() {
        String forgetPasswordCode = UUID.randomUUID().toString().substring(0, 6);
        return forgetPasswordCode;
    }

}
