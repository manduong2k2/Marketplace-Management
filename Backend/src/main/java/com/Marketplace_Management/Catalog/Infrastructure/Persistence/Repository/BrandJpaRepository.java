package com.Marketplace_Management.Catalog.Infrastructure.Persistence.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Marketplace_Management.Catalog.Infrastructure.Persistence.Entity.BrandEntity;

public interface BrandJpaRepository extends JpaRepository<BrandEntity, UUID> {
    Optional<BrandEntity> findByName(String name);
}
