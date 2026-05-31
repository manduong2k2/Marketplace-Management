package com.StoreManagement.Order.Application.DTO.Commands.Cart;

import java.util.UUID;

import lombok.Data;

@Data
public class CheckoutCartCommand {
    private UUID userId;
}
