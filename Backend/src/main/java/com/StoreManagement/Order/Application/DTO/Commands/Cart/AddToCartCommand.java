package com.StoreManagement.Order.Application.DTO.Commands.Cart;

import java.util.UUID;

import lombok.Data;

@Data
public class AddToCartCommand {
    private UUID userId;
    private UUID productId;
    private int quantity;
}
