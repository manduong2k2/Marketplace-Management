package com.Marketplace_Management.Auth.Repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Marketplace_Management.Auth.Constants.OAuthProvider;
import com.Marketplace_Management.Auth.Entities.OAuthInfoEntity;

public interface OAuthInfoJpaRepository extends JpaRepository<OAuthInfoEntity, UUID> {
    Optional<OAuthInfoEntity> findByOauthProviderAndOauthProviderSubject(OAuthProvider provider, String subject);
    List<OAuthInfoEntity> findByUserIdIn(Collection<UUID> userIds);
}
