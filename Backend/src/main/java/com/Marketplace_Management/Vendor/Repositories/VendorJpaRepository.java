package com.Marketplace_Management.Vendor.Repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Marketplace_Management.Vendor.Entities.VendorEntity;

public interface VendorJpaRepository extends JpaRepository<VendorEntity, UUID> {
    Optional<VendorEntity> findByUserId(UUID userId);
}
