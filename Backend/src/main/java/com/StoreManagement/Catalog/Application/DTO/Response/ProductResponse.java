package com.StoreManagement.Catalog.Application.DTO.Response;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.StoreManagement.Catalog.Domain.Models.Product;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "name", "code", "price", "stock", "brandId", "description"})
public class ProductResponse {
    private UUID id;
    private String name;
    private String code;
    private double price;
    private int stock;
    private UUID brandId;
    private String description;
    private String status;
    private List<String> images;
    
    public ProductResponse(Product product, String baseUrl) {
        this.id = product.getId();
        this.name = product.getName();
        this.code = product.getCode();
        this.price = product.getPrice().getValue();
        this.stock = product.getStock();
        this.brandId = product.getBrandId();
        this.description = product.getDescription();
        this.status = product.getStatus();
        this.images = product.getFiles().stream().map(file -> baseUrl + "/" + file.getUrl()).toList();
    }
}
