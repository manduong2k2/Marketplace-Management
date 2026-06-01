package com.StoreManagement.Order.Domain.Models.Cart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.StoreManagement.Order.Domain.Constants.CartStatusEnum;
import com.StoreManagement.Shared.Domain.AggregateRoot;

public class Cart extends AggregateRoot<UUID> {
    private UUID userId;
    private CartStatus status;
    private List<CartItem> items;

    public Cart() {
        super(null);
        this.items = new ArrayList<>();
    }

    public Cart(UUID id, UUID userId) {
        super(id);
        this.userId = userId;
        this.status = new CartStatus(CartStatusEnum.NEW);
        this.items = new ArrayList<>();
    }

    public Cart(UUID id, UUID userId, CartStatusEnum status, List<CartItem> items) {
        super(id);
        this.userId = userId;
        this.status = new CartStatus(status);
        this.items = new ArrayList<>(items);
    }

    // Business methods
    
    public void addItem(CartItem newItem) {
        if (!this.status.canAddItems()) {
            throw new IllegalStateException("Cannot add items to a cart with status: " + this.status.getValue());
        }

        Optional<CartItem> existing = this.items.stream()
            .filter(i -> i.getProductId().equals(newItem.getProductId()))
            .findFirst();

        if (existing.isPresent()) {
            existing.get().increaseQuantity(newItem.getQuantity());
        } else {
            this.items.add(newItem);
        }
    }

    public void removeItem(UUID productId) {
        if (!this.status.canAddItems()) {
            throw new IllegalStateException("Cannot remove items from a cart with status: " + this.status.getValue());
        }
        boolean removed = this.items.removeIf(i -> i.getProductId().equals(productId));
        if (!removed) {
            throw new IllegalArgumentException("Item not found in cart: " + productId);
        }
    }

    public void updateItemQuantity(UUID productId, int quantity) {
        if (!this.status.canAddItems()) {
            throw new IllegalStateException("Cannot update items in a cart with status: " + this.status.getValue());
        }
        CartItem item = this.items.stream()
            .filter(i -> i.getProductId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Item not found in cart: " + productId));

        if (quantity <= 0) {
            this.items.remove(item);
        } else {
            item.updateQuantity(quantity);
        }
    }

    public void clear() {
        if (!this.status.canAddItems()) {
            throw new IllegalStateException("Cannot clear a cart with status: " + this.status.getValue());
        }
        this.items.clear();
    }

    public void checkout() {
        if (!this.status.canCheckout()) {
            throw new IllegalStateException("Cart cannot be checked out with status: " + this.status.getValue());
        }
        if (this.items.isEmpty()) {
            throw new IllegalStateException("Cannot checkout an empty cart");
        }
        this.status.setValue(CartStatusEnum.CHECKED_OUT.name());
    }

    public void abandon() {
        this.status.setValue(CartStatusEnum.ABANDONED.name());
    }

    public void reactivate() {
        this.status.setValue(CartStatusEnum.NEW.name());
    }

    public void expire() {
        this.status.setValue(CartStatusEnum.EXPIRED.name());
    }

    public void changeStatus(String status) {
        this.status.setValue(status);
    }

    public long getTotalItemCount() {
        return this.items.stream().count();
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    // Base methods

    public UUID getUserId() {
        return userId;
    }

    public CartStatus getStatus() {
        return status;
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}
