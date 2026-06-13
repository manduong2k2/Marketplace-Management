package com.Marketplace_Management.Cart.DTOs.Messages;

import java.util.UUID;

public class ProductOutStockMessage {
    private UUID productVariantId;
    
    public ProductOutStockMessage() {}
    
    public ProductOutStockMessage(UUID productVariantId) {
        this.productVariantId = productVariantId;
    }
    
    public UUID getProductVariantId() {
        return productVariantId;
    }
    
    public void setProductVariantId(UUID productVariantId) {
        this.productVariantId = productVariantId;
    }
}
