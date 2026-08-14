package com.Marketplace_Management.Delivery.Entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Shared.Entities.UuidEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "deliveries")
@EqualsAndHashCode(callSuper = false)
public class DeliveryEntity extends UuidEntity{
    private UUID orderId;
    private UUID userId;
    private String status;
    private AddressEntity shippingAddress;
    private String recipientName;
    private String recipientPhone;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private String trackingNumber;
    private String deliveryMethod;
    private double deliveryFee;
    private String deliveryNotes;
    private List<DeliveryItemEntity> items;
}
