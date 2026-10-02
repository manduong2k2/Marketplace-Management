package com.Marketplace_Management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Marketplace_ManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(Marketplace_ManagementApplication.class, args);
    }
}
