package com.StoreManagement.Catalog.Application.DTO.Response;

import java.util.List;
import java.util.UUID;

import com.StoreManagement.Catalog.Domain.Models.ProductVariant;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // For Jackson deserialization
@AllArgsConstructor
@JsonPropertyOrder({"id", "name", "code", "stock", "price", "images"})
public class ProductVariantResponse {
    private UUID id;
    private String name;
    private String code;
    private int stock;
    private double price;
    private List<String> images;

    public ProductVariantResponse(ProductVariant variant, String baseUrl) {
        this.id = variant.getId();
        this.name = variant.getName();
        this.code = variant.getCode();
        this.stock = variant.getStock();
        this.price = variant.getPrice().getValue();
        this.images = variant.getFiles() != null ? variant.getFiles().stream().map(file -> baseUrl + "/" + file.getUrl()).toList() : null;
    }
}
