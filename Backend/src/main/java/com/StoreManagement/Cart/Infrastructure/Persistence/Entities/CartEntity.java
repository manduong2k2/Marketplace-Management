package com.StoreManagement.Cart.Infrastructure.Persistence.Entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.StoreManagement.Shared.Infrastructure.Persistence.UuidEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "carts")
@Data
@EqualsAndHashCode(callSuper = true)
public class CartEntity extends UuidEntity {
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "status", nullable = false)
    private String status;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItemEntity> items = new ArrayList<>();
}
