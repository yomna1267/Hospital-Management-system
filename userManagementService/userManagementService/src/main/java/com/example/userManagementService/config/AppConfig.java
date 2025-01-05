package com.example.userManagementService.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public Queue statusQueue() {
        logger.info("Creating statusQueue...");
        return new Queue("statusQueue", true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("statusExchange");
    }

    @Bean
    public Binding binding(Queue statusQueue, TopicExchange exchange) {
        return BindingBuilder.bind(statusQueue).to(exchange).with("statusRoutingKey");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @Bean
    public Queue securityQueue() {
        logger.info("Creating securityQueue...");
        return new Queue("securityQueue", true);
    }

    @Bean
    public TopicExchange securityExchange() {
        return new TopicExchange("securityExchange");
    }

    @Bean
    public Binding securityBinding(Queue securityQueue, TopicExchange securityExchange) {
        return BindingBuilder.bind(securityQueue).to(securityExchange).with("securityRoutingKey");
    }
}