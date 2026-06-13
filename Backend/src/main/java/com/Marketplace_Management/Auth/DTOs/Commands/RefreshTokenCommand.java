package com.Marketplace_Management.Auth.DTOs.Commands;

import com.Marketplace_Management.Auth.DTOs.Request.RefreshTokenRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenCommand {
    private String refreshToken;

    public static RefreshTokenCommand fromRequest(RefreshTokenRequest request) {
        return new RefreshTokenCommand(
            request.getRefreshToken()
        );
    }
}
