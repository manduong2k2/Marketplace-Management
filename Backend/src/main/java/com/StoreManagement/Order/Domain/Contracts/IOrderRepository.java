package com.StoreManagement.Order.Domain.Contracts;

import java.util.Optional;
import java.util.UUID;

import com.StoreManagement.Order.Application.DTO.Commands.ListOrderCommand;
import com.StoreManagement.Order.Application.DTO.Responses.HistoryResponse;
import com.StoreManagement.Order.Domain.Models.Order;
import com.StoreManagement.Shared.Application.DTO.Responses.PaginatedResponse;

public interface IOrderRepository {
    PaginatedResponse<HistoryResponse> findAll(ListOrderCommand command);
    Order create(Order order);
    Optional<Order> findById(UUID id);
    Order updateStatus(UUID id, String status);
    void delete(UUID id);
    PaginatedResponse<HistoryResponse> findByUserIdWithFilters(UUID userId, ListOrderCommand command);
}
