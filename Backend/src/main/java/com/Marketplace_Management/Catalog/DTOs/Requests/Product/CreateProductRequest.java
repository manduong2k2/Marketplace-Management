package com.Marketplace_Management.Catalog.DTOs.Requests.Product;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Catalog.Constants.ProductStatusEnum;
import com.Marketplace_Management.Shared.Annotation.Rules.Distinct;
import com.Marketplace_Management.Shared.Annotation.Rules.Exist;
import com.Marketplace_Management.Shared.Annotation.Rules.In;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest {
    @NotBlank(message = "Product name is required")
    @Size(min = 1, max = 100, message = "Product name must be between 1 and 100 characters")
    private String name;

    @NotNull(message = "Brand ID is required")
    @Exist(table = "brands", column = "id", message = "Brand not found", type = UUID.class)
    @org.hibernate.validator.constraints.UUID
    private String brandId;
    
    @Nullable
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;
    
    @Nullable
    @Distinct(message = "Each category ID must be unique")
    private List<@Exist(table = "categories", column = "id", message = "Category not found", type = UUID.class) @org.hibernate.validator.constraints.UUID String> categoryIds;
    
    @In(enumClass = ProductStatusEnum.class, message = "Invalid product status")
    private String status;
    
    @Distinct(field = "optionIds", message = "Variants must have unique option list")
    private List<@Valid CreateProductVariantRequest> variants;

    private List<@Valid CreateProductOptionRequest> options;
}


