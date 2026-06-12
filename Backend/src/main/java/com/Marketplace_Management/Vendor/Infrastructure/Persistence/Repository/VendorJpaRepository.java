package com.Marketplace_Management.Vendor.Infrastructure.Persistence.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Marketplace_Management.Vendor.Infrastructure.Persistence.Entity.VendorEntity;

public interface VendorJpaRepository extends JpaRepository<VendorEntity, UUID> {
    Optional<VendorEntity> findByUserId(UUID userId);
}
