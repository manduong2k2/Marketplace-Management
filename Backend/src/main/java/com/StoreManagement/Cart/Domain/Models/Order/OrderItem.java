package com.StoreManagement.Cart.Domain.Models.Order;

import java.util.UUID;

import com.StoreManagement.Shared.Domain.Entity;

public class OrderItem extends Entity<UUID>{
    private UUID productId;
    private Quantity quantity;
    private ProductSnapShot snapShot;
    private double total;

    public OrderItem() {
        super(null);
    }

    public OrderItem(UUID id, UUID productId, int quantity, ProductSnapShot snapShot) {
        super(id);
        this.productId = productId;
        this.quantity = new Quantity(quantity);
        this.snapShot = snapShot;
        this.total = this.calculateTotal();
    }

    //Business methods

    public void plusOne() {
        this.quantity.setValue(this.quantity.getValue()+1);
    }

    public void minusOne() {
        this.quantity.setValue(this.quantity.getValue()-1);
    }

    public double calculateTotal() {
        return this.quantity.getValue() * this.snapShot.getProductPrice();
    }

    public double getTotal() {
        return this.total;
    }

    //Base methods

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public UUID getProductId() {
        return productId;
    }
    
    public int getQuantity() {
        return quantity.getValue();
    }

    public void setQuantity(int quantity) {
        this.quantity.setValue(quantity);
    }

    public ProductSnapShot getSnapShot() {
        return snapShot;
    }

    public void setSnapShot(ProductSnapShot snapShot) {
        this.snapShot = snapShot;
    }
}
