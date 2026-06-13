package com.Marketplace_Management.Cart.DTOs.Messages;

import java.util.UUID;

public class ProductDeletedMessage {
    private UUID productVariantId;
    
    public ProductDeletedMessage() {}
    
    public ProductDeletedMessage(UUID productVariantId) {
        this.productVariantId = productVariantId;
    }
    
    public UUID getProductVariantId() {
        return productVariantId;
    }
    
    public void setProductVariantId(UUID productVariantId) {
        this.productVariantId = productVariantId;
    }
}
