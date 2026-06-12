package com.Marketplace_Management.Cart.Application.DTO.Commands;

import java.util.UUID;

import lombok.Data;

@Data
public class CheckoutCartCommand {
    private UUID userId;
}
