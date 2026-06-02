package com.StoreManagement.Order.Domain.Contracts;

import java.util.UUID;

import com.StoreManagement.Catalog.Application.DTO.Response.PaginatedResponse;
import com.StoreManagement.Order.Application.DTO.Commands.Order.ListOrderCommand;
import com.StoreManagement.Order.Application.DTO.Commands.Order.PlaceOrderCommand;
import com.StoreManagement.Order.Application.DTO.Responses.Order.HistoryResponse;
import com.StoreManagement.Order.Application.DTO.Responses.Order.OrderResponse;

public interface IOrderService {
    PaginatedResponse<HistoryResponse> list(ListOrderCommand command);
    OrderResponse findById(UUID id);
    PaginatedResponse<HistoryResponse> listByUser(ListOrderCommand command);
    OrderResponse placeOrder(PlaceOrderCommand command);
}
