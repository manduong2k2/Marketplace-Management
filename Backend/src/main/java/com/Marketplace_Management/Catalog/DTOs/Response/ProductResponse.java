package com.Marketplace_Management.Catalog.DTOs.Response;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.Marketplace_Management.Catalog.DTOs.Response.Short.ProductVariantShortResponse;
import com.Marketplace_Management.Catalog.Models.Product;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Data
@AllArgsConstructor
@NoArgsConstructor // For Jackson deserialization
@JsonPropertyOrder({"id", "name", "brand", "categories", "description", "status", "options", "variants"})
public class ProductResponse {
    private UUID id;
    private String name;
    private BrandResponse brand;
    private List<CategoryResponse> categories;
    private String description;
    private String status;
    private List<ProductVariantShortResponse> variants;
    private List<ProductOptionResponse> options;
    
    public ProductResponse(Product product) {
        if(product == null) {
            return;
        }

        this.id = product.getId();
        this.name = product.getName();
        this.brand = product.getBrand() != null ? new BrandResponse(product.getBrand()) : null;
        this.categories = product.getCategories() != null ? product.getCategories().stream().map(category -> new CategoryResponse(category)).toList() : null;
        this.description = product.getDescription();
        this.status = product.getStatus();
        this.options = product.getOptions() != null ? product.getOptions().stream().map(option -> new ProductOptionResponse(option)).toList() : null;
        this.variants = product.getVariants() != null ? product.getVariants().stream().map(variant -> new ProductVariantShortResponse(variant)).toList() : null;
    }

    public ProductResponse withUrl(String url) {
        this.getVariants().forEach(v -> v.withUrl(url));
        return this;
    }
}
