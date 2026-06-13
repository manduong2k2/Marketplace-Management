package com.Marketplace_Management.Catalog.Models;

import java.util.UUID;

import com.Marketplace_Management.Shared.Domain.AggregateRoot;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Brand extends AggregateRoot<UUID> {
    private String name;
    private String image;
    private String description;

    public Brand(UUID id, String name, String image, String description) {
        super(id);
        this.name = name;
        this.image = image;
        this.description = description;
    }
}