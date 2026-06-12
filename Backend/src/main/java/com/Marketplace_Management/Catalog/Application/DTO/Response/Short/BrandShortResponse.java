package com.Marketplace_Management.Catalog.Application.DTO.Response.Short;

import java.util.UUID;

import com.Marketplace_Management.Catalog.Domain.Models.Brand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "name", "image", "description"})
public class BrandShortResponse {
    private UUID id;
    private String name;

    public BrandShortResponse(Brand brand, String baseUrl) {
        this.id = brand.getId();
        this.name = brand.getName();
    }
}
