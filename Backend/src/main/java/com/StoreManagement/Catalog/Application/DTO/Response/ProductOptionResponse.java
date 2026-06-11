package com.StoreManagement.Catalog.Application.DTO.Response;

import com.StoreManagement.Catalog.Domain.Models.ProductOption;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@JsonPropertyOrder({"id", "name", "value"})
@NoArgsConstructor // For Jackson deserialization
public class ProductOptionResponse {
    private int id;
    private String name;
    private String value;

    public ProductOptionResponse(ProductOption option) {
        this.id = option.getId();
        this.name = option.getName();
        this.value = option.getValue();
    }
}
