package com.Marketplace_Management.Vendor.DTOs.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCollectionRequest {
    @NotBlank(message = "Collection name is required")
    private String name;

    @NotNull(message = "Display order is required")
    private Integer displayOrder;
}   
