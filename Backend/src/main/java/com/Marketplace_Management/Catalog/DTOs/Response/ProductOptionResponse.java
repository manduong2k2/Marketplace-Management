package com.Marketplace_Management.Catalog.DTOs.Response;

import com.Marketplace_Management.Catalog.Models.ProductOption;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@JsonPropertyOrder({"id", "name", "value"})
@NoArgsConstructor // For Jackson deserialization
public class ProductOptionResponse {
    private Integer id;
    private String name;
    private String value;

    public ProductOptionResponse(ProductOption option) {
        this.id = option.getId();
        this.name = option.getName();
        this.value = option.getValue();
    }
}
