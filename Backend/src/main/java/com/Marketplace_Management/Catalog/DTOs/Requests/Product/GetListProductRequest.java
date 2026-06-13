package com.Marketplace_Management.Catalog.DTOs.Requests.Product;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Shared.Annotation.Rules.Distinct;
import com.Marketplace_Management.Shared.Annotation.Rules.Exist;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListProductRequest {
    private int page = 0;
    private int size = 10;
    private String sortBy = "name";
    private String sortOrder = "asc";

    @Nullable
    @Distinct(message = "Each category ID must be unique")
    private List<
    @Exist(table = "categories", column = "id", message = "Category not found", type = UUID.class) 
    @org.hibernate.validator.constraints.UUID 
    String> categoryIds;

    @Nullable
    @Size(max = 100, message = "Search query must not exceed 100 characters")
    private String search;

    @Nullable
    @Exist(table = "brands", column = "id", message = "Brand not found", type = UUID.class)
    @org.hibernate.validator.constraints.UUID
    private String brandId;

    private String status;
}
