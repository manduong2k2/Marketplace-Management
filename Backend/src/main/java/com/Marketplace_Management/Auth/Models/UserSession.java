package com.Marketplace_Management.Auth.Models;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** An active login: which tokens were issued to whom, from where, until when. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {
    private UUID id;
    private UUID userId;
    private String ipAddress;
    private Instant loginAt;
    private String accessJti;
    private Instant accessExpiresAt;
    private String refreshJti;
    private Instant refreshExpiresAt;
}
