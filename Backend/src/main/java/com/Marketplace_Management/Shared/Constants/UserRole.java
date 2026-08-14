package com.Marketplace_Management.Shared.Constants;

public final class UserRole {
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    public static final String VENDOR = "VENDOR";
    
    private UserRole() {
        throw new AssertionError("Utility class");
    }
}
