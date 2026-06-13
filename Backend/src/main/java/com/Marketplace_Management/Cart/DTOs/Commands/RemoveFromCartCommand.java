package com.Marketplace_Management.Cart.DTOs.Commands;

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
