package com.StoreManagement.Order.Application.Controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.StoreManagement.Auth.Domain.Constants.Http;
import com.StoreManagement.Order.Application.Contracts.ICartService;
import com.StoreManagement.Order.Application.DTO.Commands.Cart.AddToCartCommand;
import com.StoreManagement.Order.Application.DTO.Commands.Cart.RemoveFromCartCommand;
import com.StoreManagement.Order.Application.DTO.Commands.Cart.UpdateCartItemCommand;
import com.StoreManagement.Order.Application.DTO.Requests.Cart.AddItemRequest;
import com.StoreManagement.Order.Application.DTO.Requests.Cart.UpdateItemRequest;
import com.StoreManagement.Order.Application.DTO.Responses.Cart.CartResponse;
import com.StoreManagement.Order.Domain.Models.Cart.Cart;
import com.StoreManagement.Shared.Application.Annotation.Auth.Authenticated;
import com.StoreManagement.Shared.Infrastructure.Security.SecurityUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private ICartService cartService;

    @Authenticated
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyCart() {

        UUID userId = SecurityUtils.currentUserId();
        Cart cart = cartService.getByUserId(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);

        if (cart == null) {
            response.put("cart", null);
            response.put("message", "Cart is empty");
        } else {
            response.put("cart", CartResponse.from(cart));
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/items")
    @Authenticated
    public ResponseEntity<CartResponse> addItem(@Valid @RequestBody AddItemRequest request) {
        UUID userId = SecurityUtils.currentUserId();

        AddToCartCommand command = new AddToCartCommand();
        command.setUserId(userId);
        command.setProductId(request.getProductId());
        command.setQuantity(request.getQuantity());

        CartResponse response = CartResponse.from(cartService.addItem(command));

        return ResponseEntity.status(Http.OK).body(response);
    }

    @Authenticated
    @PatchMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateItem(
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateItemRequest request) {

        UUID userId = SecurityUtils.currentUserId();

        UpdateCartItemCommand command = new UpdateCartItemCommand();
        command.setUserId(userId);
        command.setProductId(productId);
        command.setQuantity(request.getQuantity());

        CartResponse response = CartResponse.from(cartService.updateItem(command));

        return ResponseEntity.status(Http.OK).body(response);
    }

    @Authenticated
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable UUID productId) {
        UUID userId = SecurityUtils.currentUserId();

        RemoveFromCartCommand command = new RemoveFromCartCommand();
        command.setUserId(userId);
        command.setProductId(productId);

        CartResponse response = CartResponse.from(cartService.removeItem(command));

        return ResponseEntity.status(Http.OK).body(response);
    }
}
