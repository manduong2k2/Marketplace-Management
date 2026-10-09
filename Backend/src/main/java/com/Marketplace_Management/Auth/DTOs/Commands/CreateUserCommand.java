package com.Marketplace_Management.Auth.DTOs.Commands;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Auth.Constants.UserStatus;
import com.Marketplace_Management.Auth.DTOs.Request.CreateUserRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserCommand {
    private String email;
    private String password;
    private String name;
    private String phone;
    private String status;
    private List<UUID> roleIds;

    public static CreateUserCommand fromRequest(CreateUserRequest request) {
        return new CreateUserCommand(
            request.getEmail().trim(),
            request.getPassword(),
            blankToNull(request.getName()),
            blankToNull(request.getPhone()),
            request.getStatus() == null ? UserStatus.ACTIVE : request.getStatus(),
            request.getRoleIds() == null ? List.of() : request.getRoleIds()
        );
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
