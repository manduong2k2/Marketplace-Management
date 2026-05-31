package com.StoreManagement.Order.Application.DTO.Requests.Cart;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddItemRequest {

    @NotNull(message = "productId is required")
    private UUID productId;

    @Min(value = 1, message = "quantity must be at least 1")
    private int quantity;
}
