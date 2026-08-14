package com.Marketplace_Management.Delivery.Models;

import java.util.Objects;

import com.Marketplace_Management.Shared.Models.ValueObject;

public class DeliveryStatus extends ValueObject {
    private String value; // PENDING, CONFIRMED, IN_TRANSIT, DELIVERED, CANCELLED, RETURNED
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliveryStatus that = (DeliveryStatus) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    // Business logic:
    // - canConfirm() (from PENDING)
    // - canShip() (from CONFIRMED)
    // - canDeliver() (from IN_TRANSIT)
    // - canCancel() (from PENDING, CONFIRMED)
    // - canReturn() (from DELIVERED)
}
