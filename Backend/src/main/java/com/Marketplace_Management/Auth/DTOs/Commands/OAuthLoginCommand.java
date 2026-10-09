package com.Marketplace_Management.Auth.DTOs.Commands;

import com.Marketplace_Management.Auth.Constants.OAuthProvider;
import com.Marketplace_Management.Auth.DTOs.Request.OAuthLoginRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuthLoginCommand {
    private OAuthProvider provider;
    private String credential;

    public static OAuthLoginCommand fromRequest(OAuthProvider provider, OAuthLoginRequest request) {
        return new OAuthLoginCommand(provider, request.getCredential());
    }
}
