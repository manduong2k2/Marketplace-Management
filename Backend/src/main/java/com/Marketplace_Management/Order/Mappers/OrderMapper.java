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
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setUserId(order.getUserId());
        entity.setStatus(order.getStatus());
        entity.setName(order.getName());
        entity.setPhone(order.getPhone());
        entity.setAddress(order.getAddress());
        entity.setNote(order.getNote());
        entity.setItems(order.getItems().stream().map(item -> toOrderItemEntity(item, entity)).toList());
        entity.setTotal(order.getTotal());

        return entity;
    }

    private OrderItemEntity toOrderItemEntity(OrderItem item, OrderEntity entity) {
        OrderItemEntity itemEntity = new OrderItemEntity(
            item.getId(),
            item.getProductId(),
            item.getQuantity(),
            item.getProductName(),
            item.getProductSku(),
            item.getProductPrice(),
            item.getProductImages(),
            item.getProductDescription()
        );

        itemEntity.setTotal(item.calculateTotal());

        itemEntity.setOrder(entity);

        return itemEntity;
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
