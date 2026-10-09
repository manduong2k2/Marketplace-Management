package com.Marketplace_Management.Auth.DTOs.Commands;

import com.Marketplace_Management.Auth.DTOs.Request.UpdateUserRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserCommand {
    private String name;
    private String phone;
    private String status;
    private String password; // null = keep current password

    public static UpdateUserCommand fromRequest(UpdateUserRequest request) {
        return new UpdateUserCommand(
            blankToNull(request.getName()),
            blankToNull(request.getPhone()),
            request.getStatus(),
            request.getPassword() == null || request.getPassword().isEmpty() ? null : request.getPassword()
        );
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
