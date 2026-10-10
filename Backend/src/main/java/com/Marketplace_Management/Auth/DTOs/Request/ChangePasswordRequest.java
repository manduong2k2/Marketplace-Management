package com.Marketplace_Management.Auth.DTOs.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "Current password must not be empty")
    private String currentPassword;

    @NotBlank(message = "New password must not be empty")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String newPassword;
}
