package com.Marketplace_Management.Auth.Infrastructure.Persistence.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Marketplace_Management.Auth.Infrastructure.Persistence.Entity.EmailVerifyToken;

public interface IEmailVerifyTokenRepository extends JpaRepository<EmailVerifyToken, String> {
    public EmailVerifyToken findByEmail(String email);
}
