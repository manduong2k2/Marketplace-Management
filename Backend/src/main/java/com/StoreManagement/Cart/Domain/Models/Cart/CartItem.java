package com.StoreManagement.Cart.Domain.Models.Cart;

import java.util.List;
import java.util.UUID;
import com.StoreManagement.Shared.Domain.Entity;

public class CartItem extends Entity<UUID> {
    private UUID productVariantId;
    private Quantity quantity;
    private String productCode;
    private String productDescription;
    private String productName;
    private double productPrice;
    private List<String> productImage;

    public CartItem() {
        super(null);
    }

    public CartItem(UUID id, UUID productVariantId, int quantity) {
        super(id);
        this.productVariantId = productVariantId;
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

    public UUID getProductVariantId() {
        return productVariantId;
    }

    public void setProductVariantId(UUID productVariantId) {
        this.productVariantId = productVariantId;
    }

    public int getQuantity() {
        return quantity.getValue();
    }

    public void setQuantity(int quantity) {
        this.quantity.setValue(quantity);
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductCode() {
        return productCode;
    }
    
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    
    public String getProductDescription() {
        return productDescription;
    }
    
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public List<String> getProductImage() {
        return productImage;
    }

    public void setProductImage(List<String> productImage) {
        this.productImage = productImage;
    }
}
