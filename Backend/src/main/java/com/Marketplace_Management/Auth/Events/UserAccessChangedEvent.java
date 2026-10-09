package com.Marketplace_Management.Auth.Events;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * What these users may access has changed (roles granted/revoked, role code changed,
 * deactivated, deleted). Their live access tokens must be revoked so the next request
 * gets 401 and the client refreshes into a token with the current roles.
 * Handled after commit by UserObserver.
 */
public record UserAccessChangedEvent(Collection<UUID> userIds, String reason) {
    public static UserAccessChangedEvent of(UUID userId, String reason) {
        return new UserAccessChangedEvent(List.of(userId), reason);
    }
}
