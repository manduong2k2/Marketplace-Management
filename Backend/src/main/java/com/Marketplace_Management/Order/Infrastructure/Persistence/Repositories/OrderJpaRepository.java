package com.Marketplace_Management.Order.Infrastructure.Persistence.Repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Marketplace_Management.Order.Infrastructure.Persistence.Entities.OrderEntity;

import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
    List<OrderEntity> findByUserId(UUID userId);
}
