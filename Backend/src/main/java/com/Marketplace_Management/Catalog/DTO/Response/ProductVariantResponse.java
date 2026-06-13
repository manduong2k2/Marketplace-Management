package com.Marketplace_Management.Catalog.DTO.Response;

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

    public ProductVariantResponse(ProductVariant variant, String baseUrl) {
        this.id = variant.getId();
        this.name = variant.getName();
        this.code = variant.getCode();
        this.stock = variant.getStock();
        this.price = variant.getPrice().getValue();
        this.images = variant.getFiles() != null ? variant.getFiles().stream().map(file -> baseUrl + "/" + file.getUrl()).toList() : null;
        this.optionList = variant.getOptionList();
        System.out.println("optionList: "+ variant.getOptionList());
        this.product = new ProductResponse(variant.getProduct(), baseUrl);
        this.options = variant.getOptions() != null ? variant.getOptions().stream().map(option -> new ProductOptionResponse(option)).toList() : null;
    }
}
