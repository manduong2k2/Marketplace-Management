package com.StoreManagement.Order.Application.Contracts;

import java.util.UUID;

import com.StoreManagement.Catalog.Application.DTO.Response.PaginatedResponse;
import com.StoreManagement.Order.Application.DTO.Commands.ListOrderCommand;
import com.StoreManagement.Order.Application.DTO.Commands.PlaceOrderCommand;
import com.StoreManagement.Order.Application.DTO.Responses.HistoryResponse;
import com.StoreManagement.Order.Application.DTO.Responses.OrderResponse;

public interface IOrderService {
    PaginatedResponse<HistoryResponse> list(ListOrderCommand command);
    OrderResponse findById(UUID id);
    PaginatedResponse<HistoryResponse> listByUser(ListOrderCommand command);
    OrderResponse placeOrder(PlaceOrderCommand command);
}
