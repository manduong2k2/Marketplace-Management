package com.StoreManagement.Order.Infrastructure.Mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.StoreManagement.Order.Domain.Constants.OrderStatusEnum;
import com.StoreManagement.Order.Domain.Models.Order.Order;
import com.StoreManagement.Order.Domain.Models.Order.OrderItem;
import com.StoreManagement.Order.Domain.Models.Order.ProductSnapShot;
import com.StoreManagement.Order.Infrastructure.Persistence.Entities.OrderItemEntity;
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

        List<OrderItemEntity> itemEntities = order.getItems().stream()
                .map(item -> toItemJpa(item, entity))
                .toList();

        entity.setItems(itemEntities);
        return entity;
    }

    private OrderItemEntity toItemJpa(OrderItem item, OrderEntity orderEntity) {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(item.getId());
        entity.setOrder(orderEntity);
        entity.setProductId(item.getProductId());
        entity.setQuantity(item.getQuantity());

        if (item.getSnapShot() != null) {
            entity.setSnapShot(toSnapShotJpa(item.getSnapShot(), entity));
        }

        return entity;
    }

    private ProductSnapShotEntity toSnapShotJpa(ProductSnapShot snapShot, OrderItemEntity itemEntity) {
        ProductSnapShotEntity entity = new ProductSnapShotEntity();
        entity.setId(snapShot.getId());
        entity.setOrderItem(itemEntity);
        entity.setProductId(snapShot.getProductId());
        entity.setBrand(snapShot.getBrand());
        entity.setCategories(snapShot.getCategories());
        entity.setPrice(snapShot.getPrice());
        entity.setCode(snapShot.getCode());
        entity.setName(snapShot.getName());
        return entity;
    }

    public Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(this::toItemDomain)
                .toList();

        return new Order(
                entity.getId(),
                entity.getUserId(),
                OrderStatusEnum.valueOf(entity.getStatus()),
                items,
                entity.getName(),
                entity.getPhone(),
                entity.getAddress(),
                entity.getNote()
        );
    }

    private OrderItem toItemDomain(OrderItemEntity entity) {
        OrderItem item = new OrderItem(
                entity.getId(),
                entity.getProductId(),
                entity.getQuantity()
        );

        if (entity.getSnapShot() != null) {
            item.setSnapShot(toSnapShotDomain(entity.getSnapShot()));
        }

        return item;
    }

    private ProductSnapShot toSnapShotDomain(ProductSnapShotEntity entity) {
        return new ProductSnapShot(
                entity.getId(),
                entity.getProductId(),
                entity.getCode(),
                entity.getName(),
                entity.getBrand(),
                entity.getCategories(),
                entity.getPrice()
        );
    }
}
