package com.Marketplace_Management.Delivery.Models;

import java.util.UUID;

import com.Marketplace_Management.Shared.Models.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class DeliveryItem extends Entity<UUID> {

    private UUID deliveryId;
    private UUID orderItemId;
    private UUID productVariantId;
    private String productName;
    private String productSku;
    private int quantity;
    private int delivered;
}
