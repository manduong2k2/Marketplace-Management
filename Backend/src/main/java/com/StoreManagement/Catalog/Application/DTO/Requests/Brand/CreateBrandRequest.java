package com.StoreManagement.Catalog.Application.DTO.Requests.Brand;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBrandRequest {
    @NotBlank(message = "Brand name is required")
    @Size(min = 1, max = 100, message = "Brand name must be between 1 and 100 characters")
    private String name;
    
    @Nullable
    private String image;
    
    @Nullable
    @Size(max = 500)
    private String description;
}
