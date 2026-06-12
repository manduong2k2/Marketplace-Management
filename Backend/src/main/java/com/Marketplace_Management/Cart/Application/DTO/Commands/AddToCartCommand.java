package com.Marketplace_Management.Cart.Application.DTO.Commands;

import java.util.UUID;

import com.Marketplace_Management.Cart.Application.DTO.Requests.AddItemRequest;
import com.Marketplace_Management.Shared.Infrastructure.Security.SecurityUtils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddToCartCommand {
    private UUID userId;
    private UUID productVariantId;
    private int quantity;

    public AddToCartCommand() {
    }

    public static AddToCartCommand fromRequest(AddItemRequest request) {
        return new AddToCartCommand(SecurityUtils.currentUserId(), request.getProductVariantId(), request.getQuantity());
    }
}
