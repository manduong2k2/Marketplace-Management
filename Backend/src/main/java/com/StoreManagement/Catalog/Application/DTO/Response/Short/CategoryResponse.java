package com.StoreManagement.Catalog.Application.DTO.Response.Short;

import java.util.UUID;

import com.StoreManagement.Catalog.Domain.Models.Category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Data
@AllArgsConstructor
@NoArgsConstructor // For Jackson deserialization
@JsonPropertyOrder({"id", "name", "image", "description"})
public class CategoryResponse {
    private UUID id;
    private String name;

    public CategoryResponse(Category category, String baseUrl) {
        this.id = category.getId();
        this.name = category.getName();
    }
}
