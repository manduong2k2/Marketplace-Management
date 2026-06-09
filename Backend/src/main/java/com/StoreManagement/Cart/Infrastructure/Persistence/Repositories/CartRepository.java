package com.StoreManagement.Cart.Infrastructure.Persistence.Repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.StoreManagement.Cart.Domain.Contracts.ICartRepository;
import com.StoreManagement.Cart.Domain.Models.Cart.Cart;
import com.StoreManagement.Cart.Infrastructure.Mappers.CartMapper;
import com.StoreManagement.Cart.Infrastructure.Persistence.Entities.CartEntity;

@Repository
public class CartRepository implements ICartRepository {

    private final JpaCartRepository jpa;
    private final CartMapper mapper;

    public CartRepository(JpaCartRepository jpa, CartMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public List<Cart> findAll() {
        return jpa.findAll().stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public Cart create(Cart cart) {
        CartEntity entity = mapper.toEntity(cart);
        CartEntity saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Cart> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Cart> findByUserId(UUID userId) {
        return jpa.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public Cart update(Cart cart) {
        CartEntity entity = mapper.toEntity(cart);
        CartEntity saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    public List<Cart> findByItemsProductVariantId(UUID productVariantId) {
        return jpa.findByItemsProductVariantId(productVariantId).stream()
            .map(mapper::toDomain)
            .toList();
    }
}
