package com.StoreManagement.Auth.Infrastructure.Persistence.Seeder;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.StoreManagement.Auth.Domain.Constants.Message;
import com.StoreManagement.Auth.Domain.Constants.UserStatus;
import com.StoreManagement.Auth.Domain.Contract.IRoleRepository;
import com.StoreManagement.Auth.Domain.Contract.IUserRepository;
import com.StoreManagement.Auth.Domain.Models.Role;
import com.StoreManagement.Auth.Domain.Models.User;

import jakarta.transaction.Transactional;

@Component
public class AdminSeeder {
    private final PasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    
    public AdminSeeder(IUserRepository userRepository, PasswordEncoder passwordEncoder, IRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }
    
    @Transactional
    public void seedAdmin() {
        if (userRepository.findByEmail("admin@storemanagement.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@storemanagement.com");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setStatus(UserStatus.ACTIVE);
            admin.setName("Admin");
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByCode("ADMIN").orElseThrow(() -> new RuntimeException(Message.ROLE_NOT_FOUND)));
            admin.setRoles(roles);
            this.userRepository.save(admin);
        }
    }
}
