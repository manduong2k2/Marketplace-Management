package com.StoreManagement.Shared.Domain.Constants;

public final class UserRole {
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    
    private UserRole() {
        throw new AssertionError("Utility class");
    }
}
