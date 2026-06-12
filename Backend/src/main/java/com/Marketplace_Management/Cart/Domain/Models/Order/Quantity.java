package com.Marketplace_Management.Cart.Domain.Models.Order;

import java.util.Objects;

import com.Marketplace_Management.Shared.Domain.ValueObject;

public class Quantity extends ValueObject{
    private int value;

    public Quantity() {
        this.value = 0;
    }

    public Quantity(int value) {
        this.setValue(value);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if(value < 0)
            throw new IllegalArgumentException("Quantity must be positive");
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity that = (Quantity) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
