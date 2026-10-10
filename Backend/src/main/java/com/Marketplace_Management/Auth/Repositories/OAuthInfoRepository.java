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
        return OAuthInfo.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .oauthProvider(e.getOauthProvider())
                .oauthProviderSubject(e.getOauthProviderSubject())
                .createdAt(e.getCreatedAt())
                .build();
    }

    private OAuthInfoEntity toEntity(OAuthInfo d) {
        return OAuthInfoEntity.builder()
                .id(d.getId())
                .userId(d.getUserId())
                .oauthProvider(d.getOauthProvider())
                .oauthProviderSubject(d.getOauthProviderSubject())
                .createdAt(d.getCreatedAt())
                .build();
    }
}
