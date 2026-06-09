package com.StoreManagement.Cart.Application.DTO.Commands;

import java.util.UUID;

import lombok.Data;

@Data
public class RemoveFromCartCommand {
    private UUID userId;
    private UUID productVariantId;

    public RemoveFromCartCommand(UUID userId, UUID productVariantId) {
        this.userId = userId;
        this.productVariantId = productVariantId;
    }
}
