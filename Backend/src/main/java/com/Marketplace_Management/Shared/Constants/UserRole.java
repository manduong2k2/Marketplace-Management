package com.Marketplace_Management.Shared.Constants;

import java.util.Set;

public final class UserRole {
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    public static final String VENDOR = "VENDOR";

    /** Built-in roles the application logic depends on: read-only (cannot be edited or deleted). */
    public static final Set<String> SYSTEM = Set.of(ADMIN, USER, VENDOR);
    
    private UserRole() {
        throw new AssertionError("Utility class");
    }
}
