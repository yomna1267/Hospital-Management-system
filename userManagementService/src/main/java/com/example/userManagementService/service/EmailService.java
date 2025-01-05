package com.example.userManagementService.service;


import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final JavaMailSenderImpl mailSender;

    public String sendForgetPasswordCode(String to, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("jasmine.hajjaj@gizasystems.onmicrosoft.com");
        message.setTo(to);
        message.setSubject("Reset ur password");

        message.setText("Reset password code : " + otp + "\n" +
                        "It is valid for 5 minutes");

        mailSender.send(message);
        return otp;
    }


}
