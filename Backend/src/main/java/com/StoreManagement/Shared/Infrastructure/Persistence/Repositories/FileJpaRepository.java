package com.StoreManagement.Shared.Infrastructure.Persistence.Repositories;

import org.springframework.stereotype.Repository;

import com.StoreManagement.Shared.Infrastructure.Persistence.Entity.FileEntity;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface FileJpaRepository extends JpaRepository<FileEntity, UUID> {
    void deleteByUrl(String url);
}
