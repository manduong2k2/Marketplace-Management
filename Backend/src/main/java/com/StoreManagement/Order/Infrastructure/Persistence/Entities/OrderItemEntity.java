package com.StoreManagement.Order.Infrastructure.Persistence.Entities;

import java.util.UUID;

import com.StoreManagement.Shared.Infrastructure.Persistence.JpaEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderItemEntity extends JpaEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "total", nullable = false)
    private double total;

    @OneToOne(
        mappedBy = "orderItem",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    private ProductSnapShotEntity snapShot;

    public OrderItemEntity(UUID id, UUID productId, int quantity, ProductSnapShotEntity snapShot) {
        this.setId(id);
        this.productId = productId;
        this.quantity = quantity;
        this.snapShot = snapShot;
    }
}
