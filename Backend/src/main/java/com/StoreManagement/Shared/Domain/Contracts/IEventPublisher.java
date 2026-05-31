package com.StoreManagement.Shared.Domain.Contracts;

import com.StoreManagement.Shared.Domain.DomainEvent;
import com.StoreManagement.Shared.Infrastructure.Event.EventOptions;

public interface IEventPublisher {
    void publish(DomainEvent event, EventOptions options);
}
