package com.Marketplace_Management.Shared.Infrastructure.Persistence.Repositories;

import com.Marketplace_Management.Shared.Infrastructure.Persistence.Entity.FileEntity;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileJpaRepository extends JpaRepository<FileEntity, UUID> {
    void deleteByUrl(String url);
}
