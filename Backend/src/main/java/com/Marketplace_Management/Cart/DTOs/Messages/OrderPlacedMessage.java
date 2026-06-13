package com.Marketplace_Management.Cart.DTOs.Messages;

import java.util.UUID;

public class OrderPlacedMessage {
    private UUID userId;
    
    public OrderPlacedMessage() {}
    
    public OrderPlacedMessage(UUID userId) {
        this.userId = userId;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
