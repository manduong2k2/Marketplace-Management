package com.StoreManagement.Order.Infrastructure.Persistence.Entities;

import java.util.List;
import java.util.UUID;

import com.StoreManagement.Shared.Infrastructure.Persistence.JpaEntity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_snapshots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductSnapShotEntity extends JpaEntity {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItemEntity orderItem;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "brand")
    private String brand;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "product_snapshot_categories",
        joinColumns = @JoinColumn(name = "snapshot_id")
    )
    @Column(name = "category")
    private List<String> categories;

    @Column(name = "price", nullable = false)
    private double price;
}
