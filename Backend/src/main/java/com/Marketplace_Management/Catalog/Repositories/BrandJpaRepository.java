package com.Marketplace_Management.Catalog.Repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Marketplace_Management.Catalog.Entities.BrandEntity;

public interface BrandJpaRepository extends JpaRepository<BrandEntity, UUID> {
    Optional<BrandEntity> findByName(String name);
}
