package com.Marketplace_Management.Order.Contracts;

import java.util.UUID;

import com.Marketplace_Management.Order.DTO.Commands.ListOrderCommand;
import com.Marketplace_Management.Order.DTO.Commands.PlaceOrderCommand;
import com.Marketplace_Management.Order.DTO.Responses.HistoryResponse;
import com.Marketplace_Management.Order.DTO.Responses.OrderResponse;
import com.Marketplace_Management.Shared.Application.DTO.Responses.PaginatedResponse;

public interface IOrderService {
    PaginatedResponse<HistoryResponse> list(ListOrderCommand command);
    OrderResponse findById(UUID id);
    PaginatedResponse<HistoryResponse> listByUser(ListOrderCommand command);
    OrderResponse placeOrder(PlaceOrderCommand command);
}
