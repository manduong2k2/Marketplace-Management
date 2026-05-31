package com.StoreManagement.Auth.Infrastructure.Persistence.Seeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

@Component
public class DatabaseSeeder {
    
    @Autowired
    private AdminSeeder adminSeeder;
    
    @Autowired
    private RoleSeeder roleSeeder;
    
    @Bean
    @Transactional
    public CommandLineRunner seed() {
        return args -> {
            roleSeeder.seedRoles();
            adminSeeder.seedAdmin();
        };
    }
}
