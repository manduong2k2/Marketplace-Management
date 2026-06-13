package com.Marketplace_Management.Shared.Security;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.Marketplace_Management.Shared.Constants.UserRole;

public class SecurityUtils {
    public static UUID currentUserId() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            return null;
        }

        return ((UserPrincipal) authentication.getPrincipal()).getId();
    }

    public static List<String> currentUserRoles() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            return null;
        }

        return ((UserPrincipal) authentication.getPrincipal()).getRoles().stream()
                .map(role -> role.getAuthority())
                .toList();
    }

    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            return false;
        }

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return principal.getRoles().stream()
                .anyMatch(role -> role.getAuthority().equals(UserRole.ADMIN));
    }
}
