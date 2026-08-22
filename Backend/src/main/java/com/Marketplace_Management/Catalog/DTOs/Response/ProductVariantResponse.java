package com.Marketplace_Management.Catalog.DTOs.Response;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Catalog.Models.ProductVariant;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // For Jackson deserialization
@AllArgsConstructor
@JsonPropertyOrder({"id", "name", "code", "stock", "price", "images", "options"})
public class ProductVariantResponse {
    private UUID id;
    private String name;
    private String code;
    private int stock;
    private double price;
    private List<String> images;
    private String optionList;
    private ProductResponse product;
    private List<ProductOptionResponse> options;

    public ProductVariantResponse(ProductVariant variant) {
        this.id = variant.getId();
        this.name = variant.getName();
        this.code = variant.getSku();
        this.stock = variant.getStock();
        this.price = variant.getPrice().getValue();
        this.images = variant.getImages() != null ? variant.getImages().stream().map(image -> image.getUrl()).toList() : null;
        this.optionList = variant.getOptionList();
        this.product = variant != null && variant.getProduct() != null ? new ProductResponse(variant.getProduct()) : null;
        this.options = variant != null && variant.getOptions() != null ? variant.getOptions().stream().map(option -> new ProductOptionResponse(option)).toList() : null;
    }

    public ProductVariantResponse withUrl(String url) {
        this.images = this.images != null ? this.images.stream().map(image -> url + "/" + image).toList() : null;
        return this;
    }
}
