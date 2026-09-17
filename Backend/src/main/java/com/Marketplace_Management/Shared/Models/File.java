package com.Marketplace_Management.Shared.Models;

import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class File extends Entity<UUID>{
    private String url;
    private Object entity;
    
    public File(UUID id, String url, Object entity) {
        super(id);
        this.url = url;
        this.entity = entity;
    }

    public File(UUID id, String url) {
        super(id);
        this.url = url;
    }
}
