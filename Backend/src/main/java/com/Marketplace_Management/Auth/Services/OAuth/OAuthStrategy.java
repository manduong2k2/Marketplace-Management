package com.Marketplace_Management.Auth.Services.OAuth;

import com.Marketplace_Management.Auth.Constants.OAuthProvider;

/**
 * Verifies a credential issued by one identity provider and returns who it belongs to.
 * One implementation per provider; picked by {@link OAuthStrategyResolver}.
 */
public interface OAuthStrategy {

    OAuthProvider provider();

    /**
     * @param credential what the frontend got from the provider's SDK
     *                   (Google: ID token, Facebook: user access token)
     * @throws org.springframework.security.core.AuthenticationException invalid credential (401)
     * @throws org.springframework.web.server.ResponseStatusException     provider unreachable (503)
     */
    OAuthUserInfo verify(String credential);
}
