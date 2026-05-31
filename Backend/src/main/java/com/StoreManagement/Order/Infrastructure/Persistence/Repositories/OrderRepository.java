package com.StoreManagement.Order.Infrastructure.Persistence.Repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.StoreManagement.Order.Domain.Contracts.IOrderRepository;
import com.StoreManagement.Order.Domain.Models.Order.Order;
import com.StoreManagement.Order.Infrastructure.Persistence.Entities.OrderEntity;
import com.StoreManagement.Shared.Domain.Contracts.IMapper;

@Repository
public class OrderRepository implements IOrderRepository {

    private final OrderJpaRepository jpaRepository;
    private final IMapper<Order, OrderEntity> mapper;

    public OrderRepository(OrderJpaRepository jpaRepository, IMapper<Order, OrderEntity> mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Order> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Order create(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Order updateStatus(UUID id, String status) {
        OrderEntity entity = jpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        entity.setStatus(status);
        OrderEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}
