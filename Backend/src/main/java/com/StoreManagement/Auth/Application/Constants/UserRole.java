package com.StoreManagement.Auth.Application.Constants;

public final class UserRole {
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    
    private UserRole() {
        throw new AssertionError("Utility class");
    }
}
