package com.StoreManagement.Order.Domain.Contracts;

import java.util.Optional;
import java.util.UUID;

import com.StoreManagement.Catalog.Application.DTO.Response.PaginatedResponse;
import com.StoreManagement.Order.Application.DTO.Commands.Order.ListOrderCommand;
import com.StoreManagement.Order.Application.DTO.Responses.Order.HistoryResponse;
import com.StoreManagement.Order.Domain.Models.Order.Order;

public interface IOrderRepository {
    PaginatedResponse<HistoryResponse> findAll(ListOrderCommand command);
    Order create(Order order);
    Optional<Order> findById(UUID id);
    Order updateStatus(UUID id, String status);
    void delete(UUID id);
    PaginatedResponse<HistoryResponse> findByUserIdWithFilters(UUID userId, ListOrderCommand command);
}
