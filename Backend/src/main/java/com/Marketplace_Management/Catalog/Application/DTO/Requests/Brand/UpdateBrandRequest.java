package com.Marketplace_Management.Catalog.Application.DTO.Requests.Brand;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBrandRequest {
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private MultipartFile image;
}

