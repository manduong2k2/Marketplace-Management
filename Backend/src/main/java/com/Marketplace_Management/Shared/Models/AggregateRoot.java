package com.Marketplace_Management.Shared.Models;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot<T> extends Entity<T> {
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected AggregateRoot(T id) {
        super(id);
    }

    public void addDomainEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public List<DomainEvent> getDomainEvents() {
        return new ArrayList<>(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }
}