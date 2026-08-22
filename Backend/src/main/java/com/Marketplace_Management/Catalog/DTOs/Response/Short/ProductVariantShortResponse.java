package com.Marketplace_Management.Catalog.DTOs.Response.Short;

import java.util.Set;
import java.util.UUID;

import com.Marketplace_Management.Catalog.DTOs.Response.ProductOptionResponse;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // For Jackson deserialization
@JsonPropertyOrder({"id", "name", "stock", "price", "images", "options"})
public class ProductVariantShortResponse {
    private UUID id;
    private String name;
    private int stock;
    private double price;
    private String optionList;
    private Set<Image> images;
    private Set<ProductOptionResponse> options;

    @Data
    private static class Image {
        private UUID id;
        private String url;
    }
}
