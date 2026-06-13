package com.Marketplace_Management.Catalog.DTO.Requests.Brand;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBrandRequest {
    @NotBlank(message = "Brand name is required")
    @Size(min = 1, max = 100, message = "Brand name must be between 1 and 100 characters")
    private String name;
    
    @Nullable
    @Size(max = 500)
    private String description;

    private MultipartFile image;
}
