package com.Marketplace_Management.Auth.DTOs.Request;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Email is not editable. Roles are changed through role-grant / role-revoke. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    @NotBlank(message = "status must not be empty")
    @Pattern(regexp = "ACTIVE|INACTIVE", message = "status must be ACTIVE or INACTIVE")
    private String status;

    // Optional: leave empty to keep the current password
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
}
