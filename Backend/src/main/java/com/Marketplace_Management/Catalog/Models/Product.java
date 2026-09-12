package com.Marketplace_Management.Catalog.Models;

import java.util.Set;
import java.util.UUID;

import com.Marketplace_Management.Shared.Models.AggregateRoot;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class Product extends AggregateRoot<UUID> {
    private String name;
    private String description;
    private UUID brandId;
    private Brand brand;
    private List<UUID> categoryIds;
    private Set<Category> categories;
    private String status;
    private Set<ProductVariant> variants;
    private Set<ProductOption> options;
    private UUID vendorId;


    public boolean isArchived() {
        return status.equals("ARCHIVED");
    }
}
