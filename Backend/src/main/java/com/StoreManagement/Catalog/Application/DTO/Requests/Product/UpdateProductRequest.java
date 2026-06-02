package com.StoreManagement.Catalog.Application.DTO.Requests.Product;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.StoreManagement.Catalog.Domain.Constants.ProductStatusEnum;
import com.StoreManagement.Shared.Application.Annotation.Rules.Exist;
import com.StoreManagement.Shared.Application.Annotation.Rules.In;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequest {
    @NotBlank(message = "Product name is required")
    @Size(min = 1, max = 100, message = "Product name must be between 1 and 100 characters")
    private String name;

    @NotBlank(message = "Product code is required")
    private String code;

    @NotNull(message = "Brand ID is required")
    @Exist(table = "brands", column = "id", message = "Brand not found")
    private UUID brandId;
    
    @Nullable
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @In(enumClass = ProductStatusEnum.class, message = "Invalid product status")
    private String status;
    
    @Nullable
    private List<@Exist(table = "categories", column = "id", message = "Category not found") UUID> categoryIds;

    private List<String> imageUrls;

    private List<MultipartFile> images;
    
    private Double price;
    
    private Integer stock;
}


