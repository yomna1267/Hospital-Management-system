package com.example.workflow_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient // Enables this application to register with Eureka Server
public class WorkflowServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkflowServiceApplication.class, args);
	}

}
