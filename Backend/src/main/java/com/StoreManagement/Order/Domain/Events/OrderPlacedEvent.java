package com.StoreManagement.Order.Domain.Events;

import java.io.Serializable;
import java.util.UUID;

import com.StoreManagement.Shared.Domain.DomainEvent;

public class OrderPlacedEvent extends DomainEvent implements Serializable {
    private UUID userId;

    public OrderPlacedEvent(UUID userId) {
        super();
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }
}

