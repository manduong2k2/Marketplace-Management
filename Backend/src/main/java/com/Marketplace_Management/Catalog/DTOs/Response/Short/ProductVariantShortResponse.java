package com.Marketplace_Management.Catalog.DTOs.Response.Short;

import java.util.Set;
import java.util.UUID;

import com.Marketplace_Management.Catalog.DTOs.Response.ProductOptionResponse;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // For Jackson deserialization
@JsonPropertyOrder({"id", "name", "code", "stock", "price", "images", "options"})
public class ProductVariantShortResponse {
    private UUID id;
    private String name;
    private String code;
    private int stock;
    private double price;
    private String optionList;
    private Set<String> images;
    private Set<ProductOptionResponse> options;
    private String baseUrl;

    public ProductVariantShortResponse(UUID id, String name, String code, int stock, double price, String optionList, Set<String> images, Set<ProductOptionResponse> options, String baseUrl) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.stock = stock;
        this.price = price;
        this.optionList = optionList;
        this.images = images.stream().map(image -> baseUrl + "/" + image).collect(java.util.stream.Collectors.toSet());
        this.options = options;
    }
}
