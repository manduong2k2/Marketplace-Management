package com.Marketplace_Management.Auth.Models;

import java.util.Set;
import java.util.UUID;

import com.Marketplace_Management.Auth.Constants.UserStatus;
import com.Marketplace_Management.Shared.Models.AggregateRoot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor 
@NoArgsConstructor
@SuperBuilder
public class User extends AggregateRoot<UUID>{
    private String email;
    private String password;    
    private String status;
    private String name;
    private String avatar;
    private String phone;
    private Set<Role> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(UUID id, String email, String password, LocalDateTime createdAt) {
        super(id);
        this.email = email;
        this.password = password;
        this.status = UserStatus.DEFAULT;
        this.createdAt = createdAt;
    }
}