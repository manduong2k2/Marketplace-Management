package com.Marketplace_Management.Order.DTO.Messages;

import java.util.UUID;

public class ProductDeletedMessage {
    private UUID productId;
    
    public ProductDeletedMessage() {}
    
    public ProductDeletedMessage(UUID productId) {
        this.productId = productId;
    }
    
    public UUID getProductId() {
        return productId;
    }
    
    public void setProductId(UUID productId) {
        this.productId = productId;
    }
}
