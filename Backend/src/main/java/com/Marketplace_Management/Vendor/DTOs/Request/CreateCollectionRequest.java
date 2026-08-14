package com.Marketplace_Management.Vendor.DTOs.Request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class CreateCollectionRequest {
    @NotBlank(message = "Collection name is required")
    private String name;

    @NotNull(message = "Display order is required")
    private Integer displayOrder;
}
