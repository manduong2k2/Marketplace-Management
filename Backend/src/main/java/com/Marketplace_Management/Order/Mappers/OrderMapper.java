package com.Marketplace_Management.Order.Mappers;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Order.Entities.OrderEntity;
import com.Marketplace_Management.Order.Entities.OrderItemEntity;
import com.Marketplace_Management.Order.Entities.ProductSnapShotEntity;
import com.Marketplace_Management.Order.Models.Order;
import com.Marketplace_Management.Order.Models.OrderItem;
import com.Marketplace_Management.Order.Models.ProductSnapShot;
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
            null
        );

        itemEntity.setTotal(item.calculateTotal());

        itemEntity.setSnapShot(toProductSnapShotEntity(item.getSnapShot(), itemEntity));

        itemEntity.setOrder(entity);

        return itemEntity;
    }
    
    private ProductSnapShotEntity toProductSnapShotEntity(ProductSnapShot snapShot, OrderItemEntity itemEntity) {
        ProductSnapShotEntity snapShotEntity = new ProductSnapShotEntity(
            snapShot.getId(),
            snapShot.getProductCode(),
            snapShot.getProductName(),
            snapShot.getProductId(),
            snapShot.getProductPrice(),
            snapShot.getProductImages(),
            snapShot.getProductDescription()
        );
        
        snapShotEntity.setOrderItem(itemEntity);
        
        return snapShotEntity;
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
            .build();
    }

    private OrderItem toOrderItemDomain(OrderItemEntity entity) {
        return new OrderItem(
            entity.getId(),
            entity.getProductId(),
            entity.getQuantity(),
            toProductSnapShotDomain(entity.getSnapShot())
        );
    }
    
    private ProductSnapShot toProductSnapShotDomain(ProductSnapShotEntity entity) {
        return new ProductSnapShot(
            entity.getId(),
            entity.getProductId(),
            entity.getProductName(),
            entity.getProductCode(),
            entity.getProductPrice(),
            entity.getProductImages(),
            entity.getProductDescription()
        );
    }
}
