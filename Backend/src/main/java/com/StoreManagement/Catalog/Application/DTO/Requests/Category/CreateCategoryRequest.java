package com.StoreManagement.Catalog.Application.DTO.Requests.Category;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.StoreManagement.Shared.Application.Annotation.Rules.Exist;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryRequest {
    @NotBlank(message = "Category name is required")
    @Size(min = 1, max = 100, message = "Category name must be between 1 and 100 characters")
    private String name;
    
    @Nullable
    @Exist(table = "categories", column = "id")
    private UUID parentId;
    
    @Nullable
    private MultipartFile image;
    
    @Nullable
    @Size(max = 500)
    private String description;
}
