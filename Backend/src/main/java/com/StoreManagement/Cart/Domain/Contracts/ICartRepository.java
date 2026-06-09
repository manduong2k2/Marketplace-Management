package com.StoreManagement.Cart.Domain.Contracts;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.StoreManagement.Cart.Domain.Models.Cart.Cart;

public interface ICartRepository {
    List<Cart> findAll();
    Cart create(Cart cart);
    Optional<Cart> findById(UUID id);
    Optional<Cart> findByUserId(UUID userId);
    Cart update(Cart cart);
    void delete(UUID id);
    List<Cart> findByItemsProductVariantId(UUID productVariantId);
}
