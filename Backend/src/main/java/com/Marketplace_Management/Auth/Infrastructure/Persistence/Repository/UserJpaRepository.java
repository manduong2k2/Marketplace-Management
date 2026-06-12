package com.Marketplace_Management.Auth.Infrastructure.Persistence.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Marketplace_Management.Auth.Infrastructure.Persistence.Entity.UserEntity;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findById(UUID id);
}
