package com.StoreManagement.Order.Domain.Models.Cart;

import java.util.UUID;
import com.StoreManagement.Shared.Domain.Entity;

public class CartItem extends Entity<UUID> {
    private UUID productId;
    private Quantity quantity;

    public CartItem() {
        super(null);
    }

    public CartItem(UUID id, UUID productId, int quantity) {
        super(id);
        this.productId = productId;
        this.quantity = new Quantity(quantity);
    }

    // Business methods

    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.quantity.setValue(this.quantity.getValue() + amount);
    }

    public void decreaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        int newQty = this.quantity.getValue() - amount;
        if (newQty < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity.setValue(newQty);
    }

    public void updateQuantity(int quantity) {
        this.quantity.setValue(quantity);
    }

    // Base methods

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity.getValue();
    }

    public void setQuantity(int quantity) {
        this.quantity.setValue(quantity);
    }
}
