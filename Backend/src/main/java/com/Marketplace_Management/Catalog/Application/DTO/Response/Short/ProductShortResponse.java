package com.Marketplace_Management.Catalog.Application.DTO.Response.Short;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.Marketplace_Management.Catalog.Domain.Models.Product;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.Marketplace_Management.Catalog.Application.DTO.Response.ProductOptionResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor // For Jackson deserialization
@JsonPropertyOrder({"id", "name", "brand", "categories", "description", "status", "options", "variants"})
public class ProductShortResponse {
    private UUID id;
    private String name;
    private List<ProductVariantShortResponse> variants;
    private List<ProductOptionResponse> options;
    
    public ProductShortResponse(Product product, String baseUrl) {
        if(product == null) {
            return;
        }

        this.id = product.getId();
        this.name = product.getName();
        this.options = product.getOptions() != null ? product.getOptions().stream().map(option -> new ProductOptionResponse(option)).toList() : null;
        this.variants = product.getVariants() != null ? product.getVariants().stream().map(variant -> new ProductVariantShortResponse(variant, baseUrl)).toList() : null;
    }
}
