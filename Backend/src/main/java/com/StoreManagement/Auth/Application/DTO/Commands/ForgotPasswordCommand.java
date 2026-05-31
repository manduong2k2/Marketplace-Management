package com.StoreManagement.Auth.Application.DTO.Commands;

import com.StoreManagement.Auth.Application.DTO.Request.ForgotPasswordRequest;

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
