package com.StoreManagement.Auth.Domain.Constants;

public final class UserStatus {
    public static final String DEFAULT = "INACTIVE";

    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";
    
    private UserStatus() {
        throw new AssertionError("Utility class");
    }
}
