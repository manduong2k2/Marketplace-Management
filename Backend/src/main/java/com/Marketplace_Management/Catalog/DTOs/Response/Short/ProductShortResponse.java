package com.Marketplace_Management.Catalog.DTOs.Response.Short;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.Marketplace_Management.Catalog.DTOs.Response.ProductOptionResponse;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Data
@AllArgsConstructor
@NoArgsConstructor // For Jackson deserialization
@JsonPropertyOrder({"id", "name", "brand", "categories", "description", "status", "options", "variants"})
public class ProductShortResponse {
    private UUID id;
    private String name;
    private List<ProductVariantShortResponse> variants;
    private Set<ProductOptionResponse> options;
}
