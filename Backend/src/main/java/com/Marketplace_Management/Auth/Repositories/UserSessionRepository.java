package com.Marketplace_Management.Auth.Repositories;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.Marketplace_Management.Auth.Contracts.IUserSessionRepository;
import com.Marketplace_Management.Auth.Entities.UserSessionEntity;
import com.Marketplace_Management.Auth.Models.UserSession;

@Repository
public class UserSessionRepository implements IUserSessionRepository {
    private final UserSessionJpaRepository jpaRepository;

    public UserSessionRepository(UserSessionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public UserSession save(UserSession session) {
        return toDomain(jpaRepository.save(toEntity(session)));
    }

    @Override
    public List<UserSession> findByUserIds(Collection<UUID> userIds) {
        if (userIds.isEmpty()) {
            return List.of();
        }
        return jpaRepository.findByUserIdIn(userIds).stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public int deleteByRefreshJti(String refreshJti) {
        return jpaRepository.deleteByRefreshJti(refreshJti);
    }

    @Override
    @Transactional
    public int deleteByUserIdAndAccessJti(UUID userId, String accessJti) {
        return jpaRepository.deleteByUserIdAndAccessJti(userId, accessJti);
    }

    @Override
    @Transactional
    public int deleteByIds(Collection<UUID> ids) {
        return ids.isEmpty() ? 0 : jpaRepository.deleteByIds(ids);
    }

    @Override
    @Transactional
    public int deleteExpired(Instant now) {
        return jpaRepository.deleteExpired(now);
    }

    private UserSession toDomain(UserSessionEntity e) {
        return UserSession.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .ipAddress(e.getIpAddress())
                .loginAt(e.getLoginAt())
                .accessJti(e.getAccessJti())
                .accessExpiresAt(e.getAccessExpiresAt())
                .refreshJti(e.getRefreshJti())
                .refreshExpiresAt(e.getRefreshExpiresAt())
                .build();
    }

    private UserSessionEntity toEntity(UserSession s) {
        return UserSessionEntity.builder()
                .id(s.getId())
                .userId(s.getUserId())
                .ipAddress(s.getIpAddress())
                .loginAt(s.getLoginAt())
                .accessJti(s.getAccessJti())
                .accessExpiresAt(s.getAccessExpiresAt())
                .refreshJti(s.getRefreshJti())
                .refreshExpiresAt(s.getRefreshExpiresAt())
                .build();
    }
}
