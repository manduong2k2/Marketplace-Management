package com.Marketplace_Management.Auth.Seeders;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.Marketplace_Management.Auth.Constants.Message;
import com.Marketplace_Management.Auth.Constants.UserStatus;
import com.Marketplace_Management.Auth.Contracts.IRoleRepository;
import com.Marketplace_Management.Auth.Contracts.IUserRepository;
import com.Marketplace_Management.Auth.Models.Role;
import com.Marketplace_Management.Auth.Models.User;

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
        if (userRepository.findByEmail("admin@Marketplace_Management.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@Marketplace_Management.com");
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
