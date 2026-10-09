package com.Marketplace_Management.Auth.Entities;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One active login (token pair). Tracking only — never used to verify a token.
 * Rows are hard-deleted on logout, refresh (replaced by a new row), access change, and
 * by the daily cleanup once the access token has expired.
 * Does not extend JpaEntity on purpose: no soft delete, no entity lifecycle events.
 */
@Entity
@Table(
    name = "user_sessions",
    indexes = {
        // Revoke all sessions of a user / find one session of a user by access token
        @Index(name = "idx_user_sessions_user_access", columnList = "user_id, access_jti"),
        // Refresh: find the session of the refresh token being rotated
        @Index(name = "uk_user_sessions_refresh_jti", columnList = "refresh_jti", unique = true),
        // Daily cleanup of expired sessions
        @Index(name = "idx_user_sessions_access_expires_at", columnList = "access_expires_at"),
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    // IPv6 max length is 45
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "login_at", nullable = false)
    private Instant loginAt;

    @Column(name = "access_jti", nullable = false, length = 36)
    private String accessJti;

    @Column(name = "access_expires_at", nullable = false)
    private Instant accessExpiresAt;

    @Column(name = "refresh_jti", nullable = false, length = 36)
    private String refreshJti;

    @Column(name = "refresh_expires_at", nullable = false)
    private Instant refreshExpiresAt;
}
