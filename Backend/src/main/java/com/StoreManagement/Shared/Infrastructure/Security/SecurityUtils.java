package com.StoreManagement.Shared.Infrastructure.Security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
}
