package com.Marketplace_Management.Auth.DTOs.Request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Body of /users/role-grant/{roleId} and /users/role-revoke/{roleId}. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserIdsRequest {
    @NotEmpty(message = "userIds must not be empty")
    @Size(max = 100, message = "At most 100 users per request")
    private List<UUID> userIds;
}
