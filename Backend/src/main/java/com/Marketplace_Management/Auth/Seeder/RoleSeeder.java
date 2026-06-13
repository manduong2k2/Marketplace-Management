package com.Marketplace_Management.Auth.Seeder;

import java.util.List;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Auth.Contract.IRoleRepository;
import com.Marketplace_Management.Auth.Models.Role;

@Component
public class RoleSeeder {
    private final IRoleRepository roleRepository;

    public RoleSeeder(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    
    public void seedRoles() {
        if(roleRepository.count() == 0) {
            this.roleRepository.saveAll(List.of(
                new Role(null,"Admin", "ADMIN"),
                new Role(null,"User", "USER")
            ));
        }
    }
}
