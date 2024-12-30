package com.example.appointmentService.feign;


import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class feignConfig {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new customFeignErrorDecoder();
    }
}

