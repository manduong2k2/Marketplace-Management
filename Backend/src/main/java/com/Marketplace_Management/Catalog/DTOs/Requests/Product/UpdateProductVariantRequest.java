package com.Marketplace_Management.Catalog.DTOs.Requests.Product;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.Marketplace_Management.Shared.Annotation.Rules.Unique;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProductVariantRequest {
    @Nullable
    private UUID id;

    @NotBlank(message = "Product name is required")
    @Size(min = 1, max = 100, message = "Product name must be between 1 and 100 characters")
    private String name;

    @NotBlank(message = "Product code is required")
    @Unique(table = "products", column = "code", message = "Product code already exists")
    private String code;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    private double price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be positive")
    private int stock;

    private List<MultipartFile> images;
}
