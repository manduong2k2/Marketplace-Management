package com.StoreManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class StoreManagementApplication {
	public static void main(String[] args) {
		SpringApplication.run(StoreManagementApplication.class, args);
	}
}
