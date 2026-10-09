package com.Marketplace_Management.Auth.Entities;

import java.time.Instant;
import java.util.UUID;

import com.Marketplace_Management.Auth.Constants.OAuthProvider;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A third-party account linked to a user: (provider, provider subject) -> user.
 * The subject is the provider's stable user id ("sub" of the Google ID token, Facebook user id).
 * A user can have one link per provider; the same provider account can belong to only one user.
 */
@Entity
@Table(
    name = "oauth_infos",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_oauth_infos_provider_subject", columnNames = {"oauth_provider", "oauth_provider_subject"}),
    },
    indexes = {
        @Index(name = "idx_oauth_infos_user_id", columnList = "user_id"),
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuthInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider", nullable = false, length = 20)
    private OAuthProvider oauthProvider;

    @Column(name = "oauth_provider_subject", nullable = false, length = 255)
    private String oauthProviderSubject;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
