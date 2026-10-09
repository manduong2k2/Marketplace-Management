package com.Marketplace_Management.Auth.DTOs.Request;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuthLoginRequest {

    // What the provider's SDK returned: Google ID token (response.credential) or Facebook access token
    @NotBlank(message = "credential must not be empty")
    private String credential;
}
