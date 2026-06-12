package com.Marketplace_Management.Cart.Domain.Models.Order;

public enum OrderStatusValue {
    PENDING,
    PAID,
    PROCESSING,
    SHIPPING,
    COMPLETED,
    CANCELLED,
    EXPIRED;

    public static OrderStatusValue fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Order status cannot be empty");
        }
        try {
            return OrderStatusValue.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + value);
        }
    }
}
