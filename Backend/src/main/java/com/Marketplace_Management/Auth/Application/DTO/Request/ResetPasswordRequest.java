package com.Marketplace_Management.Auth.Application.DTO.Request;

import com.Marketplace_Management.Shared.Application.Annotation.Rules.Exist;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = "email must not be empty")
    @Email(message = "Email must be valid")
    @Exist(table = "users", column = "email", message = "Email does not exist")
    @Exist(table = "email_verify_tokens", column = "email", whereClause = "expires_at > NOW()", message = "Token has expired")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "token must not be empty")
    private String token;

    @NotBlank(message = "password must not be empty")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String newPassword;
}