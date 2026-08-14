package com.Marketplace_Management.Vendor.Repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Marketplace_Management.Vendor.Entities.CollectionEntity;

import java.util.Set;

public interface CollectionJpaRepository extends JpaRepository<CollectionEntity, UUID> {
    Set<CollectionEntity> findByVendorId(UUID vendorId);
}
