package com.Marketplace_Management.Cart.DTOs.Responses;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Cart.Models.Cart.CartItem;
import com.Marketplace_Management.Catalog.DTOs.Response.ProductOptionResponse;

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
    private List<ProductOptionResponse> productOptions;

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
        response.setProductOptions(item.getProductOptions());
        return response;
    }
}
