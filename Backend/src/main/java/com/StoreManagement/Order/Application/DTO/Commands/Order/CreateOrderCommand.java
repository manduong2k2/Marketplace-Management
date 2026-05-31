package com.StoreManagement.Order.Application.DTO.Commands.Order;

import java.util.List;
import java.util.UUID;

import com.StoreManagement.Order.Domain.Models.Order.OrderItem;

import lombok.Data;

@Data
public class CreateOrderCommand{
    private UUID cartId;
    private UUID userId;
    private List<OrderItem> items;
}
