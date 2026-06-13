package com.Marketplace_Management.Shared.Repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Marketplace_Management.Shared.Entities.FileEntity;

public interface FileJpaRepository extends JpaRepository<FileEntity, UUID> {
    void deleteByUrl(String url);
}
