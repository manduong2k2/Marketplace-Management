package com.Marketplace_Management.Auth.DTOs.Commands;

import com.Marketplace_Management.Auth.DTOs.Request.GoogleLoginRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleLoginCommand {
    private String idToken;

    public static GoogleLoginCommand fromRequest(GoogleLoginRequest request) {
        return new GoogleLoginCommand(request.getIdToken());
    }
}
