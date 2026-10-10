package com.Marketplace_Management.Catalog.Models;

import java.util.UUID;

import com.Marketplace_Management.Shared.Models.AggregateRoot;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class Brand extends AggregateRoot<UUID> {
    private String name;
    private String image;
    private String description;

}