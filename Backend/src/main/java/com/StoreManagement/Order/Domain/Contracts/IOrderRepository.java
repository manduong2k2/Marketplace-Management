package com.StoreManagement.Order.Domain.Contracts;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.StoreManagement.Order.Domain.Models.Order.Order;

public interface IOrderRepository {
    List<Order> findAll();
    Order create(Order order);
    Optional<Order> findById(UUID id);
    Order updateStatus(UUID id, String status);
    void delete(UUID id);
}
