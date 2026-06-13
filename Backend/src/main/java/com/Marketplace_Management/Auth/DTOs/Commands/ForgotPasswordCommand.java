package com.Marketplace_Management.Auth.DTOs.Commands;

import com.Marketplace_Management.Auth.DTOs.Request.ForgotPasswordRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordCommand {
    private String email;

    public static ForgotPasswordCommand fromRequest(ForgotPasswordRequest request) {
        return new ForgotPasswordCommand(
            request.getEmail()
        );
    }
}
