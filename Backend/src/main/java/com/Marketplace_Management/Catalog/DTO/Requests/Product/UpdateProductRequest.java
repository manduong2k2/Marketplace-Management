package com.Marketplace_Management.Catalog.DTO.Requests.Product;

import java.util.List;

import com.Marketplace_Management.Catalog.Constants.ProductStatusEnum;
import com.Marketplace_Management.Shared.Application.Annotation.Rules.Distinct;
import com.Marketplace_Management.Shared.Application.Annotation.Rules.Exist;
import com.Marketplace_Management.Shared.Application.Annotation.Rules.In;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotNull(message = "Brand ID is required")
    @Exist(table = "brands", column = "id", message = "Brand not found")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Brand ID must be a valid UUID")
    private String brandId;
    
    @Nullable
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;
    
    @Nullable
    @Distinct(message = "Each category ID must be unique")
    private List<@Exist(table = "categories", column = "id", message = "Category not found") String> categoryIds;
    
    @In(enumClass = ProductStatusEnum.class, message = "Invalid product status")
    private String status;

    private List<@Valid UpdateProductVariantRequest> variants;
}


