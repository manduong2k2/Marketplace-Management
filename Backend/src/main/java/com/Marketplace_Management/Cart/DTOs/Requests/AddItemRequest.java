package com.Marketplace_Management.Cart.DTOs.Requests;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddItemRequest {

    @NotNull(message = "productVariantId is required")
    private UUID productVariantId;

    @Min(value = 1, message = "quantity must be at least 1")
    private int quantity;
}
