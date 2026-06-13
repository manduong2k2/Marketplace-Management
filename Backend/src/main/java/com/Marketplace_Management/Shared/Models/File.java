package com.Marketplace_Management.Shared.Models;

import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class File extends Entity<UUID>{
    private String url;
    private String entityType;
    private UUID entityId;
    
    public File(UUID id, String url, String entityType, UUID entityId) {
        super(id);
        this.url = url;
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public File(UUID id, String url, String entityType) {
        super(id);
        this.url = url;
        this.entityType = entityType;
    }
}
