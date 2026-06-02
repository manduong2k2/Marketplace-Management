package com.StoreManagement.Order.Infrastructure.Persistence.Entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.StoreManagement.Shared.Infrastructure.Persistence.JpaEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderEntity extends JpaEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "note", nullable = true)
    private String note;
    
    @Column(name = "total", nullable = false)
    private double total;

    @OneToMany(
        mappedBy = "order",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    private List<OrderItemEntity> items = new ArrayList<>();

    public OrderEntity(UUID id, UUID userId, String status, String name, String phone, String address, String note, List<OrderItemEntity> items, double total) {
        this.setId(id);
        this.userId = userId;
        this.status = status;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.note = note;
        this.items = items;
        this.total = total;
    }
}
