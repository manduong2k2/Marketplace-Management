package com.Marketplace_Management.Auth.DTOs.Commands;

import com.Marketplace_Management.Auth.DTOs.Request.ChangePasswordRequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangePasswordCommand {
    private String currentPassword;
    private String newPassword;

    public static ChangePasswordCommand fromRequest(ChangePasswordRequest request) {
        return ChangePasswordCommand.builder()
                .currentPassword(request.getCurrentPassword())
                .newPassword(request.getNewPassword())
                .build();
    }
}
