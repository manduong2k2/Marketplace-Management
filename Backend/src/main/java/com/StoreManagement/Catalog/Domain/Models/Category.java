package com.StoreManagement.Catalog.Domain.Models;

import java.util.UUID;

import com.StoreManagement.Shared.Domain.AggregateRoot;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Category extends AggregateRoot<UUID> {
    private String name;
    private String image;
    private UUID parentId;
    private String description;

    public Category(UUID id, String name, UUID parentId, String image, String description) {
        super(id);
        this.name = name;
        this.image = image;
        this.parentId = parentId;
        this.description = description;
    }
}