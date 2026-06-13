package com.Marketplace_Management.Catalog.DTO.Requests.Category;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListCategoryRequest {
    private int page = 0;
    private int size = 10;
    private String sortBy = "name";
    private String sortOrder = "asc";

    @Nullable
    @Size(max = 100, message = "Search query must not exceed 100 characters")
    private String search;
}
