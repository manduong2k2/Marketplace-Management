package com.Marketplace_Management.Cart.Domain.Models.Cart;

import com.Marketplace_Management.Cart.Domain.Constants.CartStatusEnum;
import com.Marketplace_Management.Shared.Domain.ValueObject;

public class CartStatus extends ValueObject {
    private String value;

    public CartStatus(CartStatusEnum value) {
        this.value = value.name();
    }

    // Business methods

    public boolean isNew() {
        return "NEW".equals(this.value);
    }

    public boolean isCheckedOut() {
        return "CHECKED_OUT".equals(this.value);
    }

    public boolean isAbandoned() {
        return "ABANDONED".equals(this.value);
    }

    public boolean isExpired() {
        return "EXPIRED".equals(this.value);
    }

    public boolean canAddItems() {
        return this.isNew();
    }

    public boolean canCheckout() {
        return this.isNew();
    }

    public boolean canBeUpdatedTo(String newStatus) {
        if (this.isCheckedOut() || this.isExpired()) {
            return false;
        }

        if (this.isNew() && newStatus.equals("CHECKED_OUT")) {
            return true;
        }

        if (this.isNew() && newStatus.equals("ABANDONED")) {
            return true;
        }

        if (this.isAbandoned() && newStatus.equals("ACTIVE")) {
            return true;
        }

        if (this.isNew() && newStatus.equals("EXPIRED")) {
            return true;
        }

        if (this.isAbandoned() && newStatus.equals("EXPIRED")) {
            return true;
        }

        return false;
    }

    // Base methods

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Cart status cannot be empty");
        }
        if (!CartStatusEnum.isValid(value)) {
            throw new IllegalArgumentException("Invalid cart status: " + value);
        }
        if (!this.canBeUpdatedTo(value)) {
            throw new IllegalArgumentException(
                "Invalid cart status transition from " + this.value + " to " + value
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
        CartStatus that = (CartStatus) obj;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
