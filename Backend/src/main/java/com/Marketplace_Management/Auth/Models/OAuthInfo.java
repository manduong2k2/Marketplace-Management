package com.Marketplace_Management.Auth.Models;

import java.time.Instant;
import java.util.UUID;

import com.Marketplace_Management.Auth.Constants.OAuthProvider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** A third-party (Google, Facebook…) account linked to a user. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthInfo {
    private UUID id;
    private UUID userId;
    private OAuthProvider oauthProvider;
    private String oauthProviderSubject;
    private Instant createdAt;
}
