package com.StoreManagement.Cart.Application.Contracts;

import java.util.UUID;

import com.StoreManagement.Cart.Application.DTO.Commands.AddToCartCommand;
import com.StoreManagement.Cart.Application.DTO.Commands.CheckoutCartCommand;
import com.StoreManagement.Cart.Application.DTO.Commands.RemoveFromCartCommand;
import com.StoreManagement.Cart.Application.DTO.Commands.UpdateCartItemCommand;
import com.StoreManagement.Cart.Domain.Models.Cart.Cart;

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
