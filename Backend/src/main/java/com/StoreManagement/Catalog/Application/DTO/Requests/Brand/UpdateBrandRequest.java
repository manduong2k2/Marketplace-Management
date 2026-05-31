package com.StoreManagement.Catalog.Application.DTO.Requests.Brand;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBrandRequest {
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;
}

