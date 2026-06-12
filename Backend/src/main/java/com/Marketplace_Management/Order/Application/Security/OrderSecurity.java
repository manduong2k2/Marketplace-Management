package com.Marketplace_Management.Order.Application.Security;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Order.Domain.Contracts.IOrderRepository;
import com.Marketplace_Management.Order.Domain.Models.Order;
import com.Marketplace_Management.Shared.Infrastructure.Security.SecurityUtils;

@Component
public class OrderSecurity {

    private final IOrderRepository orderRepository;

    public OrderSecurity(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public boolean canViewOrder(UUID orderId) {
        UUID currentUserId = SecurityUtils.currentUserId();
        
        if (currentUserId == null) {
            return false;
        }

        if (SecurityUtils.isAdmin()) {
            return true;
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return false;
        }

        return order.getUserId().equals(currentUserId);
    }
}
