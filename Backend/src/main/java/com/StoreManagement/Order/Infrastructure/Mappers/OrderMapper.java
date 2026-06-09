package com.StoreManagement.Order.Infrastructure.Mappers;

import org.springframework.stereotype.Component;

import com.StoreManagement.Order.Infrastructure.Persistence.Entities.OrderItemEntity;
import com.StoreManagement.Order.Domain.Models.Order;
import com.StoreManagement.Order.Domain.Models.OrderItem;
import com.StoreManagement.Order.Domain.Models.ProductSnapShot;
import com.StoreManagement.Order.Infrastructure.Persistence.Entities.OrderEntity;
import com.StoreManagement.Order.Infrastructure.Persistence.Entities.ProductSnapShotEntity;
import com.StoreManagement.Shared.Domain.Contracts.IMapper;

@Component
public class OrderMapper implements IMapper<Order, OrderEntity>{
    public OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setUserId(order.getUserId());
        entity.setStatus(order.getStatus().getValue());
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
        return new Order(
            entity.getId(),
            entity.getUserId(),
            entity.getStatus(),
            entity.getItems().stream().map(this::toOrderItemDomain).toList(),
            entity.getName(),
            entity.getPhone(),
            entity.getAddress(),
            entity.getNote()
        );
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
