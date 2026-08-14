package com.Marketplace_Management.Auth.DTOs.Messages;

import java.util.UUID;

public class VendorCreatedMessage {
    private UUID userId;
    
    public VendorCreatedMessage() {}
    
    public VendorCreatedMessage(UUID userId) {
        this.userId = userId;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
