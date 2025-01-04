package com.example.userManagementService.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public Queue statusQueue() {
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
}