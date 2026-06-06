package com.StoreManagement.Catalog.Application.DTO.Response;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.StoreManagement.Catalog.Domain.Models.Category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "name", "image", "description"})
public class CategoryResponse implements Serializable {
    private UUID id;
    private String name;
    private String image;
    private String description;
    private UUID parentId;
    private List<CategoryResponse> children;

    public CategoryResponse(Category category, String baseUrl) {
        this.id = category.getId();
        this.name = category.getName();
        this.image = category.getImage() != null ? baseUrl + "/" + category.getImage() : null;
        this.description = category.getDescription();
        this.parentId = category.getParent() != null ? category.getParent().getId() : null;
        this.children = category.getChildren() != null ? category.getChildren().stream()
                .map(child -> new CategoryResponse(child, baseUrl))
                .toList() : null;
    }
}
