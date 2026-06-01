package com.StoreManagement.Catalog.Application.DTO.Response;

import java.util.UUID;

import com.StoreManagement.Catalog.Domain.Models.Category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private UUID id;
    private String name;
    private String image;

    public CategoryResponse(Category category, String baseUrl) {
        this.id = category.getId();
        this.name = category.getName();
        this.image = category.getImage() != null ? baseUrl + "/" + category.getImage() : null;
    }
}
