package com.Marketplace_Management.Shared.Models;

import java.time.LocalDateTime;
import java.util.Objects;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class Entity<T> {
    protected T id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    protected Entity() {}

    protected Entity(T id) {
        this.id = id;
    }

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
