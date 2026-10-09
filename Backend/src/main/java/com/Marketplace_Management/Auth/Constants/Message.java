package com.Marketplace_Management.Auth.Constants;

public final class Message {
    public static final String UNAUTHENTICATED = "Unauthenticated";
    public static final String CREDENTIALS = "Invalid credentials";
    public static final String FORBIDDEN = "Access denied";
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
    public static final String PHONE_EXISTS = "Phone already exists";
    public static final String ROLE_CODE_EXISTS = "Role code already exists";
    public static final String SOME_USERS_NOT_FOUND = "Some users were not found";
    public static final String SOME_ROLES_NOT_FOUND = "Some roles were not found";
    public static final String CANNOT_DELETE_SELF = "You cannot delete your own account";
    public static final String CANNOT_REVOKE_OWN_ADMIN = "You cannot revoke your own ADMIN role";
    public static final String SYSTEM_ROLE_PROTECTED = "System roles (ADMIN, USER, VENDOR) cannot be edited or deleted";
    public static final String ROLE_HAS_USERS = "This role is still assigned to users. Revoke it from all users before deleting";
    public static final String GOOGLE_TOKEN_INVALID = "Invalid Google credential";
    public static final String GOOGLE_EMAIL_NOT_VERIFIED = "Google account email is not verified";
    public static final String GOOGLE_UNAVAILABLE = "Google sign-in is temporarily unavailable. Please try again later.";
    public static final String TOKEN_REFRESHED = "Token refreshed successfully";
    public static final String PROFILE_UPDATED = "Profile updated successfully";
    public static final String LOGOUT_SUCCESS = "Logout successful";
    public static final String PASSWORD_UPDATED = "Your password has been updated successfully.";
    public static final String PASSWORD_RESET_FAILED = "Invalid or expired password reset link.";

    private Message() {
        throw new AssertionError("Utility class");
    }
}
