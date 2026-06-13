package com.Marketplace_Management.Auth.Seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

@Component
public class DatabaseSeeder {

    private final AdminSeeder adminSeeder;
    private final RoleSeeder roleSeeder;

    public DatabaseSeeder(AdminSeeder adminSeeder, RoleSeeder roleSeeder) {
        this.adminSeeder = adminSeeder;
        this.roleSeeder = roleSeeder;
    }
    
    @Bean
    @Transactional
    public CommandLineRunner seed() {
        return args -> {
            roleSeeder.seedRoles();
            adminSeeder.seedAdmin();
        };
    }
}
