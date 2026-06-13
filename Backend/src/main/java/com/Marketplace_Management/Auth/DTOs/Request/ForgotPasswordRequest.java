package com.Marketplace_Management.Auth.DTOs.Request;

import com.Marketplace_Management.Shared.Annotation.Rules.Exist;
import com.Marketplace_Management.Shared.Annotation.Rules.Unique;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest {
    @NotBlank(message = "email must not be empty")
    @Exist(table = "users", column = "email", message = "Email does not exist")
    @Unique(table = "email_verify_tokens", column = "email", whereClause = "created_at > NOW() - INTERVAL '1 hour'", message = "Please retry after 1 hour")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
}
