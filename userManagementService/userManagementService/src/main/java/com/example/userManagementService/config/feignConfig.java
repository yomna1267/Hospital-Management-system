package com.example.userManagementService.config;

import com.example.userManagementService.feign.customFeignErrorDecoder;
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
