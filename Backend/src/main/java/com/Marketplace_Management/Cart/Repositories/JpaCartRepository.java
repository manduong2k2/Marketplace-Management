package com.Marketplace_Management.Cart.Repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Marketplace_Management.Cart.Entities.CartEntity;

public interface JpaCartRepository extends JpaRepository<CartEntity, UUID> {
    Optional<CartEntity> findByUserId(UUID userId);
    List<CartEntity> findByItemsProductVariantId(UUID productId);
}
