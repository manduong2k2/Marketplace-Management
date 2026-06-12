package com.Marketplace_Management.Shared.Domain;

public abstract class ValueObject {
    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();
}
