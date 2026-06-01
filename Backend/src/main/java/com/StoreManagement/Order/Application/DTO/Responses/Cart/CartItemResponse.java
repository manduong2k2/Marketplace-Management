package com.StoreManagement.Order.Application.DTO.Responses.Cart;

import java.util.List;
import java.util.UUID;

import com.StoreManagement.Order.Domain.Models.Cart.CartItem;

import lombok.Data;

@Data
public class CartItemResponse {
    private UUID id;
    private UUID productId;
    private int quantity;
    private double subTotal;
    private String productName;
    private double productPrice;
    private List<String> productImage;

    public static CartItemResponse from(CartItem item) {
        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProductId());
        response.setQuantity(item.getQuantity());
        response.setProductName(item.getProductName());
        response.setProductPrice(item.getProductPrice());
        response.setProductImage(item.getProductImage());
        response.setSubTotal(item.getQuantity()*item.getProductPrice());
        return response;
    }
}
