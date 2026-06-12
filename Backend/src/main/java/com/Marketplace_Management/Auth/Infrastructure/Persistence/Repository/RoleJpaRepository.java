package com.Marketplace_Management.Auth.Infrastructure.Persistence.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Marketplace_Management.Auth.Infrastructure.Persistence.Entity.RoleEntity;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, UUID> {
    Optional<RoleEntity> findByCode(String code);
    List<RoleEntity> findAll();
}
