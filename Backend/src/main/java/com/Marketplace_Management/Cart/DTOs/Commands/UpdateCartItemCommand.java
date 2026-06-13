package com.Marketplace_Management.Cart.DTOs.Commands;

import java.util.UUID;

import com.Marketplace_Management.Cart.DTOs.Requests.UpdateItemRequest;
import com.Marketplace_Management.Shared.Security.SecurityUtils;

import lombok.Data;

@Data
public class UpdateCartItemCommand {
    private UUID userId;
    private UUID productVariantId;
    private int quantity;

    public static UpdateCartItemCommand fromRequest(UpdateItemRequest request) {
        UpdateCartItemCommand command = new UpdateCartItemCommand();
        command.setUserId(SecurityUtils.currentUserId());
        command.setProductVariantId(null);
        command.setQuantity(request.getQuantity());
        return command;
    }
}
