package com.Marketplace_Management.Auth.Domain.Models;

import java.util.Set;
import java.util.UUID;

import com.Marketplace_Management.Auth.Domain.Constants.UserStatus;
import com.Marketplace_Management.Shared.Domain.AggregateRoot;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
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

    public User() {
        super(null);
    }

    public User(UUID id, String email, String password, LocalDateTime createdAt) {
        super(id);
        this.email = email;
        this.password = password;
        this.status = UserStatus.DEFAULT;
        this.createdAt = createdAt;
    }

    public User(UUID id, String email, String password, String name, String avatar, String phone, String status, LocalDateTime createdAt, LocalDateTime updatedAt, Set<Role> roles) {
        super(id);
        this.email = email;
        this.password = password;
        this.name = name;
        this.avatar = avatar;
        this.phone = phone;
        this.status = status;
        this.roles = roles;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}