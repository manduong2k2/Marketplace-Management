package com.Marketplace_Management.Auth.DTOs.Response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleResponse {
    private UUID id;
    private String name;
    private String code;
    private Long usersCount;     // only in role list / detail
    private Boolean system;      // ADMIN, USER, VENDOR: read-only (cannot be edited or deleted)
    private LocalDateTime createdAt;
}
