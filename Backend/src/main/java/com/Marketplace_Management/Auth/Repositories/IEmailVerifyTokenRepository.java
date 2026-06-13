package com.Marketplace_Management.Auth.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Marketplace_Management.Auth.Entities.EmailVerifyToken;

public interface IEmailVerifyTokenRepository extends JpaRepository<EmailVerifyToken, String> {
    public EmailVerifyToken findByEmail(String email);
}
