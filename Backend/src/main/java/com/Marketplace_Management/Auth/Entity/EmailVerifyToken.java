package com.Marketplace_Management.Auth.Entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "email_verify_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerifyToken {
    @Id
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "token", nullable = false)
    private String token;
    
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}

