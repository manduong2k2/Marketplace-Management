package com.Marketplace_Management.Auth.DTOs.Messages;

import java.util.UUID;

public class VendorActivatedMessage {
    private UUID userId;
    
    public VendorActivatedMessage() {}
    
    public VendorActivatedMessage(UUID userId) {
        this.userId = userId;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
