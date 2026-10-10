package com.Marketplace_Management.Catalog.DTOs.Requests.Category;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.Marketplace_Management.Shared.Annotation.Rules.Exist;

import jakarta.annotation.Nullable;
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

    @Nullable
    @Exist(table = "categories", column = "id", type = UUID.class)
    private UUID parentId;
}

