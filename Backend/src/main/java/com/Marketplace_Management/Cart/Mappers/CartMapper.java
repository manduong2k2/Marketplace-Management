package com.Marketplace_Management.Cart.Mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Cart.Constants.CartStatusEnum;
import com.Marketplace_Management.Cart.Entities.CartEntity;
import com.Marketplace_Management.Cart.Entities.CartItemEntity;
import com.Marketplace_Management.Cart.Models.Cart.Cart;
import com.Marketplace_Management.Cart.Models.Cart.CartItem;
import com.Marketplace_Management.Shared.Contracts.EntityDomainMapper;

@Component
public class CartMapper implements EntityDomainMapper<Cart, CartEntity> {

    @Override
    public Cart toDomain(CartEntity entity) {
        if (entity == null) return null;

        List<CartItem> items = entity.getItems().stream()
            .map(this::itemToDomain)
            .toList();

        return Cart.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .status(CartStatusEnum.valueOf(entity.getStatus()))
            .items(items)
            .build();
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
        return CartItem.builder()
            .id(entity.getId())
            .productVariantId(entity.getProductVariantId())
            .quantity(entity.getQuantity())
            .build();
    }

    private CartItemEntity itemToEntity(CartItem domain, CartEntity cartEntity) {
        CartItemEntity entity = new CartItemEntity();
        entity.setId(domain.getId());
        entity.setProductVariantId(domain.getProductVariantId());
        entity.setQuantity(domain.getQuantity());
        entity.setCart(cartEntity);
        return entity;
    }
}
