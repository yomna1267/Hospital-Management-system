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

    public String sendForgetPasswordCode(String to){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("yasmeenhaggag3@gmail.com");
        message.setTo(to);
        message.setSubject("Reset ur password");
        String forgetPasswordCode = UUID.randomUUID().toString().substring(0,6);
        message.setText("Reset password code : " + forgetPasswordCode + "\n" +
                        "It is valid for 5 minutes");

        mailSender.send(message);
        return forgetPasswordCode;
    }


}
