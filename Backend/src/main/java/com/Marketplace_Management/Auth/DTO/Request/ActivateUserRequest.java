package com.Marketplace_Management.Auth.DTO.Request;

import com.Marketplace_Management.Shared.Application.Annotation.Rules.Exist;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivateUserRequest {
    @NotBlank(message = "email must not be empty")
    @Email(message = "Email must be valid")
    @Exist(table = "users", column = "email", message = "Email does not exist")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "token must not be empty")
    private String token;
}