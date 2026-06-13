package com.Marketplace_Management.Cart.Models.Order;


import com.Marketplace_Management.Cart.Constants.OrderStatusEnum;
import com.Marketplace_Management.Shared.Domain.ValueObject;

public class OrderStatus extends ValueObject{
    private String value;

    public OrderStatus(String value) {
        this.value = value;
    }

    //Business methods

    public boolean isCompleted() {
        return "COMPLETED".equals(this.value);
    }
    
    public boolean isCancelled() {
        return "CANCELLED".equals(this.value);
    }

    public boolean isExpired() {
        return "EXPIRED".equals(this.value);
    }

    public boolean isPending() {
        return "PENDING".equals(this.value);
    }
    
    public boolean isProcessing() {
        return "PROCESSING".equals(this.value);
    }
    
    public boolean isShipping() {
        return "SHIPPING".equals(this.value);
    }

    public boolean isPaid() {
        return "PAID".equals(this.value);
    }
    
    public boolean canBeCancelled() {
        return !this.isCompleted() && 
               !this.isCancelled() && 
               !this.isExpired();
    }
    
    public boolean canBePaid() {
        return this.isPending();
    }

    public boolean canBeUpdatedTo(String newStatus) {
        if (this.isCancelled() || this.isCompleted() || this.isExpired()) {
            return false;
        }

        if (this.isPending() && newStatus.equals("PAID")) {
            return true;
        }

        if (this.isPaid() && newStatus.equals("PROCESSING")) {
            return true;
        }

        if (this.isProcessing() && newStatus.equals("SHIPPING")) {
            return true;
        }

        if (this.isShipping() && newStatus.equals("COMPLETED")) {
            return true;
        }

        if (this.canBeCancelled() && newStatus.equals("CANCELLED")) {
            return true;
        }

        return false;
    }


    //Base methods

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Order status cannot be empty");
        }
        if (!OrderStatusEnum.isValid(value)) {
            throw new IllegalArgumentException("Invalid order status: " + value);
        }

        if(!this.canBeUpdatedTo(value)) {
            throw new IllegalArgumentException(
                "Invalid order status transition from " + this.value + " to " + value
            );
        }

        this.value = value;
    }
    
    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OrderStatus that = (OrderStatus) obj;
        return value.equals(that.value);
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
