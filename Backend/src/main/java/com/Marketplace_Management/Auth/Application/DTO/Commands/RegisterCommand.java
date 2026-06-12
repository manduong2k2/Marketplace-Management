package com.Marketplace_Management.Auth.Application.DTO.Commands;

import com.Marketplace_Management.Auth.Application.DTO.Request.RegisterRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterCommand {
    private String email;
    private String password;
    private String name;
    private String phone;

    public static RegisterCommand fromRequest(RegisterRequest request) {
        return new RegisterCommand(
            request.getEmail(),
            request.getPassword(),
            request.getName(),
            request.getPhone()
        );
    }
}
