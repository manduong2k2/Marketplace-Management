package com.Marketplace_Management.Auth.DTOs.Response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Admin view of a user. Never contains the password hash or provider account ids. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {
    private UUID id;
    private String email;
    private String name;
    private String avatar;
    private String phone;
    private String status;
    // Linked sign-in providers, e.g. ["FACEBOOK", "GOOGLE"]
    private List<String> oauthProviders = new ArrayList<>();
    private LocalDateTime createdAt;
    private List<RoleResponse> roles = new ArrayList<>();
}
