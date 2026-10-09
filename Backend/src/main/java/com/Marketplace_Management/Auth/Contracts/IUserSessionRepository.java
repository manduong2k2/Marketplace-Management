package com.Marketplace_Management.Auth.Contracts;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Auth.Models.UserSession;

public interface IUserSessionRepository {
    UserSession save(UserSession session);
    List<UserSession> findByUserIds(Collection<UUID> userIds);
    int deleteByRefreshJti(String refreshJti);
    int deleteByUserIdAndAccessJti(UUID userId, String accessJti);
    int deleteByIds(Collection<UUID> ids);
    /** Sessions whose access token has expired: nothing left to revoke. */
    int deleteExpired(Instant now);
}
