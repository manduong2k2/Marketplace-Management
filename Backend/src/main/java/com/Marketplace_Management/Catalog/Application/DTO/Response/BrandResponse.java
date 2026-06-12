package com.Marketplace_Management.Catalog.Application.DTO.Response;

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
public class BrandResponse {
    private UUID id;
    private String name;
    private String image;
    private String description;

    public BrandResponse(Brand brand, String baseUrl) {
        this.id = brand.getId();
        this.name = brand.getName();
        this.image = brand.getImage() != null ? baseUrl + "/" + brand.getImage() : null;
        this.description = brand.getDescription();
    }
}
