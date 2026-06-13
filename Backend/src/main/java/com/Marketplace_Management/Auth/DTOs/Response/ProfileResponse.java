package com.Marketplace_Management.Auth.DTOs.Response;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.Marketplace_Management.Auth.Models.Role;
import com.Marketplace_Management.Auth.Models.User;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonPropertyOrder({"id", "email", "name", "avatar", "phone", "status", "role", "createdAt" })
public class ProfileResponse {
    private UUID id;
    private String email;
    private String name;
    private String avatar;
    private String phone;
    private String status;
    private Set<String> roles;
    private LocalDateTime createdAt;

    public ProfileResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.avatar = user.getAvatar();
        this.phone = user.getPhone();
        this.status = user.getStatus();
        this.roles = user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet());
        this.createdAt = user.getCreatedAt();
    }
}
