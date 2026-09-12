package com.Marketplace_Management.Delivery.Models;

import java.util.UUID;

import com.Marketplace_Management.Shared.Models.AggregateRoot;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class Delivery extends AggregateRoot<UUID> {
    private UUID orderId;
    private UUID userId;
    private DeliveryStatus status;
    private Address shippingAddress;
    private String recipientName;
    private String recipientPhone;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private String trackingNumber;
    private DeliveryMethod deliveryMethod;
    private double deliveryFee;
    private String deliveryNotes;
    private List<DeliveryItem> items;
}
