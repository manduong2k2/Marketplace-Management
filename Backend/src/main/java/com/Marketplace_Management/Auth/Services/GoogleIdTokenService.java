package com.Marketplace_Management.Auth.Services;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.Marketplace_Management.Auth.Constants.Message;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

/**
 * Verifies Google Sign-In ID tokens (JWT) offline.
 * Google's public keys are downloaded once and cached by the verifier, then every token is checked locally:
 * signature, issuer (accounts.google.com), expiry and audience (our OAuth client ID).
 */
@Service
public class GoogleIdTokenService {
    private final GoogleIdTokenVerifier verifier;

    public GoogleIdTokenService(@Value("${application.google.client-id}") String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(List.of(clientId))
                .build();
    }

    public GoogleUserInfo verify(String idToken) {
        GoogleIdToken token;
        try {
            token = verifier.verify(idToken);
        } catch (IllegalArgumentException e) {
            // Malformed JWT
            token = null;
        } catch (GeneralSecurityException | IOException e) {
            // Could not fetch Google's public keys
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, Message.GOOGLE_UNAVAILABLE, e);
        }

        if (token == null) {
            throw new AuthenticationException(Message.GOOGLE_TOKEN_INVALID) {};
        }

        GoogleIdToken.Payload payload = token.getPayload();
        return new GoogleUserInfo(
                payload.getSubject(),
                payload.getEmail(),
                Boolean.TRUE.equals(payload.getEmailVerified()),
                (String) payload.get("name"),
                (String) payload.get("picture"));
    }

    public record GoogleUserInfo(String googleId, String email, boolean emailVerified, String name, String picture) {}
}
