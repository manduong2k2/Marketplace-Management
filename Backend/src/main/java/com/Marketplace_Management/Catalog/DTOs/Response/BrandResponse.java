package com.Marketplace_Management.Catalog.DTOs.Response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.Marketplace_Management.Catalog.Models.Brand;
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

    public BrandResponse(Brand brand) {
        this.id = brand.getId();
        this.name = brand.getName();
        this.image = brand.getImage() != null ? brand.getImage() : null;
        this.description = brand.getDescription();
    }

    public BrandResponse withUrl(String url) {
        if (this.image != null) {
            this.image = url + "/" + this.image;
        }
        return this;
    }
}
