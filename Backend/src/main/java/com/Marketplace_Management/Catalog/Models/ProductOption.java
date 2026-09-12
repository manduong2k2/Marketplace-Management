package com.Marketplace_Management.Catalog.Models;

import java.util.UUID;

import com.Marketplace_Management.Shared.Models.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data 
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class ProductOption extends Entity<Long> {

    private String name;
    private String value;
    private UUID productId;
    private Product product;
}
