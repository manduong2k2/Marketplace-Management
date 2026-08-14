package com.Marketplace_Management.Delivery.Models;

import java.time.LocalDateTime;
import java.util.UUID;

import com.Marketplace_Management.Shared.Models.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DeliveryTracking extends Entity<UUID> {
    
    private UUID deliveryId;
    private String status;
    private String description;
    private LocalDateTime timestamp;
    private String location;

    public DeliveryTracking() {
        super(null);
    }
}
