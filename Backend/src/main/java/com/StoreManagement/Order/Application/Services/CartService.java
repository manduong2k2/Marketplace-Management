package com.StoreManagement.Order.Application.Services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.StoreManagement.Order.Application.Contracts.ICartService;
import com.StoreManagement.Order.Application.DTO.Commands.Cart.AddToCartCommand;
import com.StoreManagement.Order.Application.DTO.Commands.Cart.CheckoutCartCommand;
import com.StoreManagement.Order.Application.DTO.Commands.Cart.RemoveFromCartCommand;
import com.StoreManagement.Order.Application.DTO.Commands.Cart.UpdateCartItemCommand;
import com.StoreManagement.Order.Domain.Contracts.ICartRepository;
import com.StoreManagement.Order.Domain.Models.Cart.Cart;
import com.StoreManagement.Order.Domain.Models.Cart.CartItem;

@Service
public class CartService implements ICartService {

    @Autowired
    private ICartRepository repository;

    @Override
    public Cart addItem(AddToCartCommand command) {
        Cart cart = repository.findByUserId(command.getUserId())
                .orElseGet(() -> {
                    try {
                        Cart newCart = new Cart(null, command.getUserId());
                        Cart saved = repository.create(newCart);
                        return saved;
                    } catch (DataIntegrityViolationException e) {
                        return repository.findByUserId(command.getUserId()).orElseThrow();
                    }
                });

        CartItem item = new CartItem(
                null,
                command.getProductId(),
                command.getQuantity());
        cart.addItem(item);
        Cart updated = repository.update(cart);

        return updated;
    }

    @Override
    public Cart removeItem(RemoveFromCartCommand command) {
        Cart cart = getByUserId(command.getUserId());
        cart.removeItem(command.getProductId());
        return repository.update(cart);
    }

    @Override
    public Cart updateItem(UpdateCartItemCommand command) {
        Cart cart = getByUserId(command.getUserId());
        cart.updateItemQuantity(command.getProductId(), command.getQuantity());
        return repository.update(cart);
    }

    @Override
    public Cart checkout(CheckoutCartCommand command) {
        Cart cart = getByUserId(command.getUserId());
        cart.checkout();
        Cart updated = repository.update(cart);

        return updated;
    }

    @Override
    public Cart getByUserId(UUID userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for user: " + userId));
    }

    @Override
    public Cart getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found: " + id));
    }
}
