package com.Marketplace_Management.Order.Mappers;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Order.Entities.OrderEntity;
import com.Marketplace_Management.Order.Entities.OrderItemEntity;
import com.Marketplace_Management.Order.Models.Order;
import com.Marketplace_Management.Order.Models.OrderItem;
import com.Marketplace_Management.Shared.Contracts.EntityDomainMapper;

@Component
public class OrderMapper implements EntityDomainMapper<Order, OrderEntity>{
    public OrderEntity toEntity(Order order) {
        OrderEntity entity = OrderEntity.builder()
            .id(order.getId())
            .userId(order.getUserId())
            .status(order.getStatus())
            .name(order.getName())
            .phone(order.getPhone())
            .address(order.getAddress())
            .note(order.getNote())
            .total(order.getTotal())
            .build();
        entity.setItems(order.getItems().stream().map(item -> toOrderItemEntity(item, entity)).toList());
        return entity;
    }

    private OrderItemEntity toOrderItemEntity(OrderItem item, OrderEntity entity) {
        return OrderItemEntity.builder()
            .id(item.getId())
            .productId(item.getProductId())
            .quantity(item.getQuantity())
            .total(item.calculateTotal())
            .productName(item.getProductName())
            .productSku(item.getProductSku())
            .productPrice(item.getProductPrice())
            .productImages(item.getProductImages())
            .productDescription(item.getProductDescription())
            .order(entity)
            .build();
    }

    public Order toDomain(OrderEntity entity) {
        return Order.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .status(entity.getStatus())
            .items(entity.getItems().stream().map(this::toOrderItemDomain).toList())
            .name(entity.getName())
            .phone(entity.getPhone())
            .address(entity.getAddress())
            .note(entity.getNote())
            .total(entity.getTotal())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    private OrderItem toOrderItemDomain(OrderItemEntity entity) {
        return OrderItem.builder()
            .id(entity.getId())
            .productId(entity.getProductId())
            .quantity(entity.getQuantity())
            .total(entity.getTotal())
            .productName(entity.getProductName())
            .productSku(entity.getProductSku())
            .productPrice(entity.getProductPrice())
            .productImages(entity.getProductImages())
            .productDescription(entity.getProductDescription())
            .build();
    }
}
