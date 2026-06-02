package com.StoreManagement.Auth.Infrastructure.Persistence.Seeder;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.StoreManagement.Auth.Domain.Contract.IRoleRepository;
import com.StoreManagement.Auth.Domain.Models.Role;

@Component
public class RoleSeeder {
    @Autowired
    public IRoleRepository roleRepository;
    
    public void seedRoles() {
        if(roleRepository.count() == 0) {
            this.roleRepository.saveAll(List.of(
                new Role(null,"Admin", "ADMIN"),
                new Role(null,"User", "USER")
            ));
        }
    }
}
