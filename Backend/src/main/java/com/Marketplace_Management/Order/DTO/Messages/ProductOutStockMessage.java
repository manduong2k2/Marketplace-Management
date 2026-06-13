package com.Marketplace_Management.Order.DTO.Messages;

import java.util.UUID;

public class ProductOutStockMessage {
    private UUID productId;
    
    public ProductOutStockMessage() {}
    
    public ProductOutStockMessage(UUID productId) {
        this.productId = productId;
    }
    
    public UUID getProductId() {
        return productId;
    }
    
    public void setProductId(UUID productId) {
        this.productId = productId;
    }
}
