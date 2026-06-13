package com.Marketplace_Management.Order.Models;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Shared.Domain.AggregateRoot;

public class Order extends AggregateRoot<UUID> {
    private UUID userId;
    private OrderStatus status;
    private List<OrderItem> items;
    private String name;
    private String phone;
    private String address;
    private String note;
    private double total;

    public Order(){
        super(null);
    }

    public Order(UUID id, UUID userId, String status, List<OrderItem> items, String name, String phone, String address, String note) {
        super(id);
        this.userId = userId;
        this.status = new OrderStatus(status);
        this.items = items;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.note = note;
        this.total = this.calculateTotal();
    }

    //Business methods;
    public void changeStatus(String status) {
        this.status.setValue(status);
    }

    public double calculateTotal() {
        return this.items.stream().mapToDouble(item -> item.calculateTotal()).sum();
    }

    public double getTotal() {
        return this.total;
    }

    //Base methods

    public UUID getUserId() {
        return userId;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
