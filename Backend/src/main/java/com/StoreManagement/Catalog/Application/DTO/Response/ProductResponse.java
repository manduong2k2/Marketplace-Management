package com.StoreManagement.Catalog.Application.DTO.Response;

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
    
    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.code = product.getCode();
        this.price = product.getPrice().getValue();
        this.stock = product.getStock();
        this.brandId = product.getBrandId();
        this.description = product.getDescription();
        this.status = product.getStatus();
    }
}
