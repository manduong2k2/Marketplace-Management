package com.StoreManagement.Auth.Domain.Constants;

public final class Message {
    public static final String UNAUTHENTICATED = "Unauthenticated";
    public static final String CREDENTIALS = "Invalid credentials";
    public static final String ACTIVATION = "Account is not active";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String ROLE_NOT_FOUND = "Role not found";

    public static final String TOKEN_INVALID = "Invalid token";
    public static final String TOKEN_EXPIRED = "Token expired";
    public static final String TOKEN_BLACKLISTED = "Token has been blacklisted";
    
    public static final String ACTIVATION_MAIL_SENT = "An email has been sent to your email address. Please verify your email to activate your account.";
    public static final String RECOVERY_MAIL_SENT = "An email has been sent to your email address. Please check your email to reset your password.";
    public static final String RECOVERY_MAIL_FAILED = "Failed to send password reset email. Please try again later.";
    public static final String ACTIVATED = "Account activated successfully";
    public static final String LOGIN_SUCCESS = "Login successful";
    public static final String TOKEN_REFRESHED = "Token refreshed successfully";
    public static final String PROFILE_UPDATED = "Profile updated successfully";
    public static final String LOGOUT_SUCCESS = "Logout successful";
    public static final String PASSWORD_UPDATED = "Your password has been updated successfully.";
    public static final String PASSWORD_RESET_FAILED = "Invalid or expired password reset link.";

    private Message() {
        throw new AssertionError("Utility class");
    }
}
