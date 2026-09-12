package com.Marketplace_Management.Catalog.DTOs.Response;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Data
@AllArgsConstructor
@NoArgsConstructor // For Jackson deserialization
@JsonPropertyOrder({ "id", "name", "brand", "status", "options", "variants" })
@Builder
public class ProductShortResponse {
    private UUID id;
    private String name;
    private Brand brand;
    private String status;
    private List<ProductVariantShortResponse> variants;
    private Set<ProductOptionResponse> options;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonPropertyOrder({ "id", "name" })
    public static class Brand {
        private UUID id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonPropertyOrder({ "id", "name", "stock", "price", "images", "options" })
    public static class ProductVariantShortResponse {
        private UUID id;
        private String name;
        private int stock;
        private double price;
        private String optionList;
        private Set<Image> images;
        private Set<ProductOptionResponse> options;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        private static class Image {
            private UUID id;
            private String url;
        }
    }

    public ProductShortResponse withUrl(String url) {
        this.variants.forEach(variant -> {
            variant.getImages().forEach(image -> {
                image.setUrl(url + '/' + image.getUrl());
            });
        });
        return this;
    }
}
