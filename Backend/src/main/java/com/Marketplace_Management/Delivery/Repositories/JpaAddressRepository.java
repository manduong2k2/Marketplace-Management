package com.Marketplace_Management.Delivery.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Marketplace_Management.Delivery.Entities.AddressEntity;

import java.util.List;
import java.util.UUID;

public interface JpaAddressRepository extends JpaRepository<AddressEntity, Long> {
    List<AddressEntity> findByUserId(UUID userId);
    AddressEntity findFirstByUserIdAndIsDefault(UUID userId, boolean isDefault);
}
