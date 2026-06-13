package com.Marketplace_Management.Cart.Contracts;

import java.util.UUID;

import com.Marketplace_Management.Cart.DTO.Commands.AddToCartCommand;
import com.Marketplace_Management.Cart.DTO.Commands.CheckoutCartCommand;
import com.Marketplace_Management.Cart.DTO.Commands.RemoveFromCartCommand;
import com.Marketplace_Management.Cart.DTO.Commands.UpdateCartItemCommand;
import com.Marketplace_Management.Cart.Models.Cart.Cart;

public interface ICartService {
    Cart addItem(AddToCartCommand command);
    Cart removeItem(RemoveFromCartCommand command);
    Cart updateItem(UpdateCartItemCommand command);
    Cart checkout(CheckoutCartCommand command);
    Cart getByUserId(UUID userId);
    Cart getById(UUID id);
    void clearCart(UUID userId);
    void removeItemsByProductVariantId(UUID productVariantId);
}
