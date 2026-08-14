package com.Marketplace_Management.Catalog.DTOs.Response.Short;

import java.util.Set;
import java.util.UUID;

import com.Marketplace_Management.Catalog.DTOs.Response.ProductOptionResponse;
import com.Marketplace_Management.Catalog.Models.ProductVariant;
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

    public ProductVariantShortResponse(ProductVariant variant) {
        this.id = variant.getId();
        this.name = variant.getName();
        this.code = variant.getSku();
        this.stock = variant.getStock();
        this.price = variant.getPrice().getValue();
        this.images = variant.getFiles() != null ? variant.getFiles().stream().map(file -> file.getUrl()).collect(java.util.stream.Collectors.toSet()) : null;
        this.optionList = variant.getOptionList();
        this.options = variant != null && variant.getOptions() != null ? variant.getOptions().stream().map(option -> new ProductOptionResponse(option)).collect(java.util.stream.Collectors.toSet()) : null;
    }

    public ProductVariantShortResponse withUrl(String url) {
        this.images = this.images != null ? this.images.stream().map(image -> url + "/" + image).collect(java.util.stream.Collectors.toSet()) : null;
        return this;
    }
}
