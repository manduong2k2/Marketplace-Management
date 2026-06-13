package com.Marketplace_Management.Auth.DTOs.Commands;

import com.Marketplace_Management.Auth.DTOs.Request.ResetPasswordRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordCommand {
    private String email;
    private String token;
    private String newPassword;

    public static ResetPasswordCommand fromRequest(ResetPasswordRequest request) {
        return new ResetPasswordCommand(
            request.getEmail(),
            request.getToken(),
            request.getNewPassword()
        );
    }
}
