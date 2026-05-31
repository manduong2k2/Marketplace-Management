package com.StoreManagement.Catalog.Application.DTO.Requests.Category;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCategoryRequest {
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;
}

