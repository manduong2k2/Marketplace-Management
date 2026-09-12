package com.Marketplace_Management.Delivery.Models;

import java.time.LocalDateTime;
import java.util.UUID;

import com.Marketplace_Management.Shared.Models.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder 
public class DeliveryTracking extends Entity<UUID> {
    
    private UUID deliveryId;
    private String status;
    private String description;
    private LocalDateTime timestamp;
    private String location;
}
