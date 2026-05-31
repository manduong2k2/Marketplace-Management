package com.StoreManagement.Order.Infrastructure.Mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.StoreManagement.Order.Domain.Constants.CartStatusEnum;
import com.StoreManagement.Order.Domain.Models.Cart.Cart;
import com.StoreManagement.Order.Domain.Models.Cart.CartItem;
import com.StoreManagement.Order.Infrastructure.Persistence.Entities.CartEntity;
import com.StoreManagement.Order.Infrastructure.Persistence.Entities.CartItemEntity;
import com.StoreManagement.Shared.Domain.Contracts.IMapper;

@Component
public class CartMapper implements IMapper<Cart, CartEntity> {

    @Override
    public Cart toDomain(CartEntity entity) {
        if (entity == null) return null;

        List<CartItem> items = entity.getItems().stream()
            .map(this::itemToDomain)
            .toList();

        return new Cart(
            entity.getId(),
            entity.getUserId(),
            CartStatusEnum.valueOf(entity.getStatus()),
            items
        );
    }

    @Override
    public CartEntity toEntity(Cart domain) {
        if (domain == null) return null;

        CartEntity entity = new CartEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setStatus(domain.getStatus().getValue());

        List<CartItemEntity> itemEntities = domain.getItems().stream()
            .map(item -> itemToEntity(item, entity))
            .toList();

        entity.setItems(new java.util.ArrayList<>(itemEntities));

        return entity;
    }

    // --- CartItem helpers ---

    private CartItem itemToDomain(CartItemEntity entity) {
        return new CartItem(
            entity.getId(),
            entity.getProductId(),
            entity.getQuantity()
        );
    }

    private CartItemEntity itemToEntity(CartItem domain, CartEntity cartEntity) {
        CartItemEntity entity = new CartItemEntity();
        entity.setId(domain.getId());
        entity.setProductId(domain.getProductId());
        entity.setQuantity(domain.getQuantity());
        entity.setCart(cartEntity);
        return entity;
    }
}
