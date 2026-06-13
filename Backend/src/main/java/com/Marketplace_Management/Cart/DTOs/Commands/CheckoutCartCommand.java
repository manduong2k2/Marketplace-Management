package com.Marketplace_Management.Cart.DTOs.Commands;

import java.util.UUID;

import lombok.Data;

@Data
public class CheckoutCartCommand {
    private UUID userId;
}
