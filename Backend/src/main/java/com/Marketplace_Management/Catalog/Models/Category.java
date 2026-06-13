package com.Marketplace_Management.Catalog.Models;

import java.util.UUID;

import com.Marketplace_Management.Shared.Domain.AggregateRoot;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class Category extends AggregateRoot<UUID> {
    private String name;
    private String image;
    private String description;
    private Category parent;
    private List<Category> children;

    public Category(UUID id, String name, String image, String description, Category parent, List<Category> children) {
        super(id);
        this.name = name;
        this.image = image;
        this.description = description;
        this.parent = parent;
        this.children = children;
    }
}