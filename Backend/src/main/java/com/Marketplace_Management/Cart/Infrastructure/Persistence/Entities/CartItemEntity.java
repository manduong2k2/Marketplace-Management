package com.Marketplace_Management.Cart.Infrastructure.Persistence.Entities;

import java.util.UUID;

import com.Marketplace_Management.Shared.Infrastructure.Persistence.UuidEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "cart_items")
@Data
@EqualsAndHashCode(callSuper = true)
public class CartItemEntity extends UuidEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cart_id", nullable = false)
    private CartEntity cart;

    @Column(name = "product_variant_id", nullable = false)
    private UUID productVariantId;

    @Column(name = "quantity", nullable = false)
    private int quantity;
}
