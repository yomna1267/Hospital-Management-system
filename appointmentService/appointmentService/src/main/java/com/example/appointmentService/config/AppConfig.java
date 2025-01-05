package com.example.appointmentService.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
<<<<<<< HEAD:userManagementService/userManagementService/src/main/java/com/example/userManagementService/config/AppConfig.java
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
=======
>>>>>>> 0903e0ddec8e7446518ca5a8aeff797fbe5a83c6:appointmentService/appointmentService/src/main/java/com/example/appointmentService/config/AppConfig.java

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
        logger.info("Creating statusQueue...");
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
<<<<<<< HEAD:userManagementService/userManagementService/src/main/java/com/example/userManagementService/config/AppConfig.java

    @Bean
    @LoadBalanced
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
=======
}
>>>>>>> 0903e0ddec8e7446518ca5a8aeff797fbe5a83c6:appointmentService/appointmentService/src/main/java/com/example/appointmentService/config/AppConfig.java
