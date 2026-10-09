package com.Marketplace_Management.Auth.Constants;

import java.util.Arrays;
import java.util.Optional;

/** Third-party identity providers a user can sign in with. Stored by name in oauth_infos.provider. */
public enum OAuthProvider {
    GOOGLE,
    FACEBOOK;

    /** "google" / "facebook" (path segment of /api/auth/oauth/{provider}), case-insensitive. */
    public static Optional<OAuthProvider> fromPath(String value) {
        return Arrays.stream(values()).filter(p -> p.name().equalsIgnoreCase(value)).findFirst();
    }
}
