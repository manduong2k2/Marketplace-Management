package com.Marketplace_Management.Delivery.Repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Marketplace_Management.Delivery.Entities.AddressEntity;

public interface JpaAddressRepository extends JpaRepository<AddressEntity, Long> {
    /** Default address first, then the most recent. */
    List<AddressEntity> findByUserIdOrderByIsDefaultDescCreatedAtDesc(UUID userId);

    Optional<AddressEntity> findByIdAndUserId(Long id, UUID userId);

    Optional<AddressEntity> findFirstByUserIdAndIsDefaultTrue(UUID userId);

    Optional<AddressEntity> findFirstByUserIdOrderByCreatedAtDesc(UUID userId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE AddressEntity a SET a.isDefault = false WHERE a.userId = :userId AND a.isDefault = true")
    int clearDefault(@Param("userId") UUID userId);
}
