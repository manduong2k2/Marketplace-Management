package com.StoreManagement.Shared.Infrastructure.Persistence.Entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.StoreManagement.Shared.Infrastructure.Persistence.JpaEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "files")
@EqualsAndHashCode(callSuper = false)
@Data
public class FileEntity extends JpaEntity {

    @Column(nullable = false)
    private String url;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    public FileEntity() {
    }

    public FileEntity(UUID id, String url, String entityType, UUID entityId) {
        this.setId(id);
        this.url = url;
        this.entityType = entityType;
        this.entityId = entityId;
    }
}
