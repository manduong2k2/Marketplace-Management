package com.Marketplace_Management.Auth.Application.DTO.Request;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "email must not be empty")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "password must not be empty")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
}
