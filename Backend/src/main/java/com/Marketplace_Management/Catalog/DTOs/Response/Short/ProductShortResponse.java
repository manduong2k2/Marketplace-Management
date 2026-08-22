package com.Marketplace_Management.Catalog.DTOs.Response.Short;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.Marketplace_Management.Catalog.DTOs.Response.ProductOptionResponse;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Data
@AllArgsConstructor
@NoArgsConstructor // For Jackson deserialization
@JsonPropertyOrder({ "id", "name", "brand", "description", "options", "variants" })
@Builder
public class ProductShortResponse {
    private UUID id;
    private String name;
    private Brand brand;
    private String description;
    private List<ProductVariantShortResponse> variants;
    private Set<ProductOptionResponse> options;

    @Data
    @JsonPropertyOrder({ "id", "name" })
    public static class Brand {
        private UUID id;
        private String name;
    }
}
