package com.StoreManagement.Cart.Application.Services;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.StoreManagement.Catalog.Application.DTO.Response.ProductResponse;
import com.StoreManagement.Catalog.Domain.Contract.IProductService;
import com.StoreManagement.Cart.Application.Contracts.ICartService;
import com.StoreManagement.Cart.Application.DTO.Commands.AddToCartCommand;
import com.StoreManagement.Cart.Application.DTO.Commands.CheckoutCartCommand;
import com.StoreManagement.Cart.Application.DTO.Commands.RemoveFromCartCommand;
import com.StoreManagement.Cart.Application.DTO.Commands.UpdateCartItemCommand;
import com.StoreManagement.Cart.Domain.Contracts.ICartRepository;
import com.StoreManagement.Cart.Domain.Models.Cart.Cart;
import com.StoreManagement.Cart.Domain.Models.Cart.CartItem;

@Service
public class CartService implements ICartService {

    private final ICartRepository repository;
    private final IProductService productService;

    public CartService(ICartRepository repository, IProductService productService) {
        this.repository = repository;
        this.productService = productService;
    }

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
                command.getProductVariantId(),
                command.getQuantity());
        cart.addItem(item);
        Cart updated = repository.update(cart);

        return updated;
    }

    @Override
    public Cart removeItem(RemoveFromCartCommand command) {
        Cart cart = getByUserId(command.getUserId());
        cart.removeItem(command.getProductVariantId());
        Cart updated = repository.update(cart);
        return updated;
    }

    @Override
    public Cart updateItem(UpdateCartItemCommand command) {
        Cart cart = getByUserId(command.getUserId());
        cart.updateItemQuantity(command.getProductVariantId(), command.getQuantity());
        Cart updated = repository.update(cart);

        for (CartItem item : updated.getItems()) {
            ProductResponse product = productService.getProduct(item.getProductVariantId());
            item.setProductName(product.getName());
            //item.setProductPrice(product.getPrice());
            //item.setProductImage(product.getImages());
        }

        return updated;
    }

    @Override
    public Cart checkout(CheckoutCartCommand command) {
        Cart cart = getByUserId(command.getUserId());
        cart.checkout();
        Cart updated = repository.update(cart);

        for (CartItem item : updated.getItems()) {
            ProductResponse product = productService.getProduct(item.getProductVariantId());
            item.setProductName(product.getName());
            //item.setProductPrice(product.getPrice());
            //item.setProductImage(product.getImages());
        }

        return updated;
    }

    @Override
    public Cart getByUserId(UUID userId) {
        Cart cart = repository.findByUserId(userId)
                .orElse(null);
        
        if (cart == null) {
            return null;
        }
        
        // Load product details for each item in the cart
        for (CartItem item : cart.getItems()) {
            ProductResponse product = productService.getProduct(item.getProductVariantId());
            item.setProductName(product.getName());
            //item.setProductPrice(product.getPrice());
            //item.setProductImage(product.getImages());
            //item.setProductCode(product.getCode());
            item.setProductDescription(product.getDescription());
        }
        
        return cart;
    }

    @Override
    public void clearCart(UUID userId) {
        Cart cart = getByUserId(userId);
        
        if(cart == null) {
            return;
        }

        cart.clear();
        repository.update(cart);
    }

    @Override
    public Cart getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found: " + id));
    }

    @Override
    public void removeItemsByProductVariantId(UUID productVariantId) {
        repository.findByItemsProductVariantId(productVariantId).forEach(cart -> {
            if (cart.getItems().stream().anyMatch(item -> item.getProductVariantId().equals(productVariantId))) {
                cart.removeItem(productVariantId);
                repository.update(cart);
            }
        });
    }
}
