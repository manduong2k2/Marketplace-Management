package com.StoreManagement.Catalog.Application.DTO.Requests.Category;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

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
    
    private MultipartFile image;
    private UUID parentId;
}

