package com.StoreManagement.Order.Application.Contracts;

import java.util.UUID;

import com.StoreManagement.Order.Application.DTO.Commands.Cart.AddToCartCommand;
import com.StoreManagement.Order.Application.DTO.Commands.Cart.CheckoutCartCommand;
import com.StoreManagement.Order.Application.DTO.Commands.Cart.RemoveFromCartCommand;
import com.StoreManagement.Order.Application.DTO.Commands.Cart.UpdateCartItemCommand;
import com.StoreManagement.Order.Domain.Models.Cart.Cart;

public interface ICartService {
    Cart addItem(AddToCartCommand command);
    Cart removeItem(RemoveFromCartCommand command);
    Cart updateItem(UpdateCartItemCommand command);
    Cart checkout(CheckoutCartCommand command);
    Cart getByUserId(UUID userId);
    Cart getById(UUID id);
    void clearCart(UUID userId);
    void removeItemsByProductId(UUID productId);
}
