package com.Marketplace_Management.Order.Models;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Shared.Models.AggregateRoot;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Order extends AggregateRoot<UUID> {
    private UUID userId;
    private OrderStatus status;
    private List<OrderItem> items;
    private String name;
    private String phone;
    private String address;
    private String note;
    private double total;

    public Order() {
        super(null);
    }

    // Business methods;
    public void changeStatus(String status) {
        this.status.setValue(status);
    }

    public double calculateTotal() {
        return this.items.stream().mapToDouble(item -> item.calculateTotal()).sum();
    }

    public double getTotal() {
        return this.total;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID userId;
        private OrderStatus status;
        private List<OrderItem> items;
        private String name;
        private String phone;
        private String address;
        private String note;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }
        
        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder status(String status) {
            this.status = new OrderStatus(status);
            return this;
        }
        
        public Builder items(List<OrderItem> items) {
            this.items = items;
            return this;
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }
        
        public Builder address(String address) {
            this.address = address;
            return this;
        }
        
        public Builder note(String note) {
            this.note = note;
            return this;
        }
        
        public Order build() {
            Order order = new Order();
            order.setId(this.id);
            order.setUserId(this.userId);
            order.setStatus(this.status);
            order.setItems(this.items);
            order.setName(this.name);
            order.setPhone(this.phone);
            order.setAddress(this.address);
            order.setNote(this.note);
            order.setTotal(this.items.stream().mapToDouble(item -> item.getTotal() * item.getQuantity()).sum());
            return order;
        }
    }
}
