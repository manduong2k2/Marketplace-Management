package com.StoreManagement.Catalog.Domain.Events;

import java.io.Serializable;
import java.util.UUID;

import com.StoreManagement.Shared.Domain.DomainEvent;

public class ProductOutStockEvent extends DomainEvent implements Serializable{
    private UUID productId;
    private String newStatus;
    
    public ProductOutStockEvent(UUID productId, String newStatus) {
        super();
        this.productId = productId;
        this.newStatus = newStatus;
    }
    
    public UUID getProductId() {
        return productId;
    }
    
    public String getNewStatus() {
        return newStatus;
    }
}
