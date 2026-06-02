package com.StoreManagement.Catalog.Domain.Events;

import java.io.Serializable;
import java.util.UUID;

import com.StoreManagement.Shared.Domain.DomainEvent;

public class ProductArchivedEvent extends DomainEvent implements Serializable {
    private UUID productId;
    
    public ProductArchivedEvent(UUID productId) {
        super();
        this.productId = productId;
    }
    
    public UUID getProductId() {
        return productId;
    }
}
