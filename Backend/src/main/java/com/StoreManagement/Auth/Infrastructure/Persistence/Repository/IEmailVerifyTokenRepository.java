package com.StoreManagement.Auth.Infrastructure.Persistence.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.StoreManagement.Auth.Infrastructure.Persistence.Entity.EmailVerifyToken;

public interface IEmailVerifyTokenRepository extends JpaRepository<EmailVerifyToken, String> {
    public EmailVerifyToken findByEmail(String email);
}
