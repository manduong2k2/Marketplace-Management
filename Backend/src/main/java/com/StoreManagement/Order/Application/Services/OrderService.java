package com.StoreManagement.Order.Application.Services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.StoreManagement.Order.Application.DTO.Commands.Order.CreateOrderCommand;
import com.StoreManagement.Order.Application.DTO.Responses.Order.OrderResponse;
import com.StoreManagement.Order.Domain.Constants.OrderStatusEnum;
import com.StoreManagement.Order.Domain.Contracts.IOrderRepository;
import com.StoreManagement.Order.Domain.Models.Order.Order;
import com.StoreManagement.Shared.Domain.Contracts.IEventPublisher;
import com.StoreManagement.Shared.Infrastructure.Event.EventOptions;

@Service
public class OrderService {
    @Autowired
    private IOrderRepository repository;

    @Autowired
    public IEventPublisher eventPublisher;

    public List<OrderResponse> list() {
        return repository.findAll().stream()
                .map(OrderResponse::from)
                .toList();
    }

    public OrderResponse findById(UUID id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        return OrderResponse.from(order);
    }

    public void create(CreateOrderCommand command) {
        Order order = new Order(null, command.getUserId(), OrderStatusEnum.PENDING, command.getItems(), command.getName(), command.getPhone(), command.getAddress(), command.getNote());
        repository.create(order);
    }

    @Async
    private void publishDomainEvents(Order order, String queue) {
        order.getDomainEvents()
                .forEach(event -> eventPublisher.publish(event, new EventOptions(queue, false)));

        order.clearDomainEvents();
    }
}
