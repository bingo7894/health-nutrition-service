package com.healthapp.nutrition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HealthNutritionApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthNutritionApplication.class, args);
    }
}

