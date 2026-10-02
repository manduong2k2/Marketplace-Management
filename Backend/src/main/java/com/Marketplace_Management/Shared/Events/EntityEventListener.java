package com.Marketplace_Management.Shared.Events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.Marketplace_Management.Shared.Entities.JpaEntity;

import jakarta.mail.MessagingException;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EntityEventListener {
    private final ApplicationEventPublisher publisher;

    @PostPersist
    public void created(JpaEntity entity) throws MessagingException {
        publisher.publishEvent(new EntityEvent<JpaEntity>(entity, EntityEvent.Type.CREATED));
    }

    @PostUpdate
    public void updated(JpaEntity entity) {
        publisher.publishEvent(new EntityEvent<JpaEntity>(entity, EntityEvent.Type.UPDATED));
    }

    @PostRemove
    public void deleted(JpaEntity entity) {
        publisher.publishEvent(new EntityEvent<JpaEntity>(entity, EntityEvent.Type.DELETED));
    }
}
