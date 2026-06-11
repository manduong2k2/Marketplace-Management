package com.StoreManagement.Catalog.Application.DTO.Requests.Product;

import java.util.List;
import java.util.UUID;

import com.StoreManagement.Catalog.Domain.Constants.ProductStatusEnum;
import com.StoreManagement.Shared.Application.Annotation.Rules.Distinct;
import com.StoreManagement.Shared.Application.Annotation.Rules.Exist;
import com.StoreManagement.Shared.Application.Annotation.Rules.In;

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


