package com.StoreManagement.Cart.Application.DTO.Commands;

import java.util.UUID;

import com.StoreManagement.Cart.Application.DTO.Requests.UpdateItemRequest;
import com.StoreManagement.Shared.Infrastructure.Security.SecurityUtils;

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
