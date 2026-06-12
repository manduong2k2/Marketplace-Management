package com.Marketplace_Management.Shared.Domain.Contracts;

import com.Marketplace_Management.Shared.Domain.DomainEvent;
import com.Marketplace_Management.Shared.Infrastructure.Event.EventOptions;

public interface IEventPublisher {
    void publish(DomainEvent event, EventOptions options);
}
