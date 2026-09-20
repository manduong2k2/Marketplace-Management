package com.Marketplace_Management.Cart.Models.Cart;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Catalog.DTOs.Response.ProductOptionResponse;
import com.Marketplace_Management.Shared.Models.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data 
@EqualsAndHashCode(callSuper = true)
public class CartItem extends Entity<UUID> {
    private UUID productVariantId;
    private int quantity;
    private String productSku;
    private String productDescription;
    private String productName;
    private double productPrice;
    private List<ProductOptionResponse> productOptions;
    private List<String> productImage;

    public CartItem(UUID id, UUID productVariantId, int quantity) {
        super(id);
        this.productVariantId = productVariantId;
        this.quantity = quantity;
    }

    // Business methods

    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        int newQty = this.quantity - amount;
        if (newQty < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = newQty;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }
}
