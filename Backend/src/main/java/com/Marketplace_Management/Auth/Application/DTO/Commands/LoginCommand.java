package com.Marketplace_Management.Auth.Application.DTO.Commands;

import com.Marketplace_Management.Auth.Application.DTO.Request.LoginRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginCommand {
    private String email;
    private String password;

    public static LoginCommand fromRequest(LoginRequest request) {
        return new LoginCommand(
            request.getEmail(),
            request.getPassword()
        );
    }
}
