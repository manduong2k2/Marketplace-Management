package com.Marketplace_Management.Auth.Repositories;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.Marketplace_Management.Auth.Constants.OAuthProvider;
import com.Marketplace_Management.Auth.Contracts.IOAuthInfoRepository;
import com.Marketplace_Management.Auth.Entities.OAuthInfoEntity;
import com.Marketplace_Management.Auth.Models.OAuthInfo;

@Repository
public class OAuthInfoRepository implements IOAuthInfoRepository {
    private final OAuthInfoJpaRepository jpaRepository;

    public OAuthInfoRepository(OAuthInfoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<OAuthInfo> findByProviderAndSubject(OAuthProvider provider, String subject) {
        return jpaRepository.findByOauthProviderAndOauthProviderSubject(provider, subject).map(this::toDomain);
    }

    @Override
    public OAuthInfo save(OAuthInfo info) {
        return toDomain(jpaRepository.save(toEntity(info)));
    }

    @Override
    public Map<UUID, List<OAuthProvider>> findProvidersByUserIds(Collection<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return jpaRepository.findByUserIdIn(userIds).stream()
                .collect(Collectors.groupingBy(
                        OAuthInfoEntity::getUserId,
                        Collectors.mapping(OAuthInfoEntity::getOauthProvider, Collectors.toList())));
    }

    private OAuthInfo toDomain(OAuthInfoEntity e) {
        return new OAuthInfo(e.getId(), e.getUserId(), e.getOauthProvider(), e.getOauthProviderSubject(), e.getCreatedAt());
    }

    private OAuthInfoEntity toEntity(OAuthInfo d) {
        return new OAuthInfoEntity(d.getId(), d.getUserId(), d.getOauthProvider(), d.getOauthProviderSubject(), d.getCreatedAt());
    }
}
