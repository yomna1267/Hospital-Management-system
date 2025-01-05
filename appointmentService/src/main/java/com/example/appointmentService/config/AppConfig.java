package com.example.appointmentService.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public Queue appointmentQueue() {
        return new Queue("appointmentQueue", true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("appointmentExchange");
    }

    @Bean
    public Binding binding(Queue appointmentQueue, TopicExchange exchange) {
        return BindingBuilder.bind(appointmentQueue).to(exchange).with("appointmentRoutingKey");
    }
    @Bean
    public Queue statusQueue() {
        return new Queue("statusQueue", true);
    }

    @Bean
    public TopicExchange exchangeStatus() {
        return new TopicExchange("statusExchange");
    }

    @Bean
    public Binding bindingStatus(Queue statusQueue, TopicExchange exchange) {
        return BindingBuilder.bind(statusQueue).to(exchange).with("statusRoutingKey");
    }
}
