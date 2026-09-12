package com.Marketplace_Management.Order.Models;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Shared.Models.AggregateRoot;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class Order extends AggregateRoot<UUID> {
    private UUID userId;
    private String status;
    private List<OrderItem> items;
    private String name;
    private String phone;
    private String address;
    private String note;
    private double total;

    // Business methods;
    public void changeStatus(String status) {
        this.status = status;
    }

    public double calculateTotal() {
        return this.items.stream().mapToDouble(item -> item.calculateTotal()).sum();
    }

    public double getTotal() {
        return this.total;
    }
}
