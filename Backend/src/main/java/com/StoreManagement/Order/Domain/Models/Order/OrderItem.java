package com.StoreManagement.Order.Domain.Models.Order;

import java.util.UUID;

import com.StoreManagement.Shared.Domain.Entity;

public class OrderItem extends Entity<UUID>{
    private String productId;
    private Quantity quantity;
    private ProductSnapShot snapShot;

    public OrderItem() {
        super(null);
    }

    public OrderItem(UUID id, String productId, int quantity) {
        super(id);
        this.productId = productId;
        this.quantity = new Quantity(quantity);
    }

    //Business methods

    public void plusOne() {
        this.quantity.setValue(this.quantity.getValue()+1);
    }

    public void minusOne() {
        this.quantity.setValue(this.quantity.getValue()-1);
    }

    public double getTotal() {
        return this.quantity.getValue() * this.snapShot.getPrice();
    }

    //Base methods
    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductId() {
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
