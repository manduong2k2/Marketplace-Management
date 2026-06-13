package com.Marketplace_Management.Auth.DTO.Commands;

import com.Marketplace_Management.Auth.DTO.Request.ActivateUserRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivateUserCommand {
    private String email;
    private String token;

    public static ActivateUserCommand fromRequest(ActivateUserRequest request) {
        return new ActivateUserCommand(
            request.getEmail(),
            request.getToken()
        );
    }
}
