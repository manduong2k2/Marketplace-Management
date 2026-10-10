package com.Marketplace_Management.Shared.Security;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.Marketplace_Management.Shared.Constants.UserRole;

/** The authenticated user of the current request (set by JwtAuthenticationFilter). */
public final class SecurityUtils {
    private SecurityUtils() {
    }

    private static Optional<UserPrincipal> principal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal
                ? Optional.of(principal)
                : Optional.empty();
    }

    public static UUID currentUserId() {
        return principal().map(UserPrincipal::getId).orElse(null);
    }

    public static String currentUserName() {
        return principal().map(UserPrincipal::getName).orElse(null);
    }

    public static List<String> currentUserRoles() {
        return principal()
                .map(p -> p.getRoles().stream().map(GrantedAuthority::getAuthority).toList())
                .orElse(null);
    }

    public static boolean isAdmin() {
        return principal()
                .map(p -> p.getRoles().stream().anyMatch(role -> UserRole.ADMIN.equals(role.getAuthority())))
                .orElse(false);
    }

    /** True when the current user owns the resource (ownerId) or is an admin. */
    public static boolean isOwnerOrAdmin(UUID ownerId) {
        UUID currentUserId = currentUserId();
        return currentUserId != null && (isAdmin() || currentUserId.equals(ownerId));
    }
}
