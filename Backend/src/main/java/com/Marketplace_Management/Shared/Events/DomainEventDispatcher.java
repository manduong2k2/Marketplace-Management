package com.Marketplace_Management.Shared.Events;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Shared.Contracts.IEventPublisher;
import com.Marketplace_Management.Shared.Models.AggregateRoot;

/** Publishes the domain events an aggregate collected (AggregateRoot.addDomainEvent) to a queue, then clears them. */
@Component
public class DomainEventDispatcher {
    private final IEventPublisher eventPublisher;

    public DomainEventDispatcher(IEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void dispatch(AggregateRoot<?> aggregate, String queue) {
        aggregate.getDomainEvents().forEach(event -> eventPublisher.publish(event, new EventOptions(queue, false)));
        aggregate.clearDomainEvents();
    }
}
