package com.Marketplace_Management.Shared.Contracts;

import com.Marketplace_Management.Shared.Events.EventOptions;
import com.Marketplace_Management.Shared.Models.DomainEvent;

public interface IEventPublisher {
    void publish(DomainEvent event, EventOptions options);
}
