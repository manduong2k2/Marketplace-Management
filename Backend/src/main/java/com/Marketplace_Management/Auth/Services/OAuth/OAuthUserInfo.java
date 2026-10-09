package com.Marketplace_Management.Auth.Services.OAuth;

import com.Marketplace_Management.Auth.Constants.OAuthProvider;

/**
 * Verified identity returned by a provider.
 * @param subject       provider's stable user id (Google "sub", Facebook user id)
 * @param email         may be null (e.g. Facebook account without an email, or permission declined)
 * @param emailVerified whether the provider vouches for the email
 */
public record OAuthUserInfo(
        OAuthProvider provider,
        String subject,
        String email,
        boolean emailVerified,
        String name,
        String picture) {
}
