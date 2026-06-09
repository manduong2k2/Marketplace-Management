package com.StoreManagement.Cart.Application.DTO.Responses;

import java.util.List;
import java.util.UUID;

import com.StoreManagement.Cart.Domain.Models.Cart.CartItem;

import lombok.Data;

@Data
public class CartItemResponse {
    private UUID id;
    private UUID productVariantId;
    private int quantity;
    private double subTotal;
    private String productCode;
    private String productDescription;
    private String productName;
    private double productPrice;
    private List<String> productImage;

    public static CartItemResponse from(CartItem item) {
        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setProductVariantId(item.getProductVariantId());
        response.setQuantity(item.getQuantity());
        response.setProductName(item.getProductName());
        response.setProductPrice(item.getProductPrice());
        response.setProductImage(item.getProductImage());
        response.setProductCode(item.getProductCode());
        response.setProductDescription(item.getProductDescription());
        response.setSubTotal(item.getQuantity()*item.getProductPrice());
        return response;
    }
}
