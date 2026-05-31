package com.StoreManagement.Order.Domain.Models.Order;

import com.StoreManagement.Shared.Domain.ValueObject;

import java.util.Objects;

public class Money extends ValueObject{
    private double amount;

    public Money(double amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money that = (Money) o;
        return Objects.equals(amount, that.amount);
    }

    public double getValue() {
        return amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}
