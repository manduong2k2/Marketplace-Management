package com.StoreManagement.Auth.Infrastructure.Persistence.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.StoreManagement.Auth.Infrastructure.Persistence.Entity.RoleEntity;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, UUID> {
    Optional<RoleEntity> findByCode(String code);
    List<RoleEntity> findAll();
}
