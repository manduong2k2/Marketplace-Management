package com.Marketplace_Management.Vendor.Events;

import java.io.Serializable;
import java.util.UUID;

import com.Marketplace_Management.Shared.Models.DomainEvent;
import com.Marketplace_Management.Vendor.Models.Vendor;

public class VendorCreatedEvent extends DomainEvent implements Serializable {
    private UUID userId;

    public VendorCreatedEvent(Vendor vendor) {
        super();
        this.userId = vendor.getUserId();
    }

    public UUID getUserId() {
        return userId;
    }
}
