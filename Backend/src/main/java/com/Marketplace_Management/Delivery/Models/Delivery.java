package com.Marketplace_Management.Delivery.Models;

import java.util.UUID;

import com.Marketplace_Management.Shared.Models.AggregateRoot;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
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

    public Delivery() {
        super(null);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

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
        
        public Builder() {
        }
        
        public Builder orderId(UUID orderId) {
            this.orderId = orderId;
            return this;
        }
        
        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder status(DeliveryStatus status) {
            this.status = status;
            return this;
        }
        
        public Builder shippingAddress(Address shippingAddress) {
            this.shippingAddress = shippingAddress;
            return this;
        }
        
        public Builder recipientName(String recipientName) {
            this.recipientName = recipientName;
            return this;
        }
        
        public Builder recipientPhone(String recipientPhone) {
            this.recipientPhone = recipientPhone;
            return this;
        }
        
        public Builder estimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
            this.estimatedDeliveryDate = estimatedDeliveryDate;
            return this;
        }
        
        public Builder actualDeliveryDate(LocalDateTime actualDeliveryDate) {
            this.actualDeliveryDate = actualDeliveryDate;
            return this;
        }
        
        public Builder trackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
            return this;
        }
        
        public Builder deliveryMethod(DeliveryMethod deliveryMethod) {
            this.deliveryMethod = deliveryMethod;
            return this;
        }
        
        public Builder deliveryFee(double deliveryFee) {
            this.deliveryFee = deliveryFee;
            return this;
        }
        
        public Builder deliveryNotes(String deliveryNotes) {
            this.deliveryNotes = deliveryNotes;
            return this;
        }
        
        public Builder items(List<DeliveryItem> items) {
            this.items = items;
            return this;
        }
        
        public Delivery build() {
            Delivery delivery = new Delivery();
            delivery.setOrderId(this.orderId);
            delivery.setUserId(this.userId);
            delivery.setStatus(this.status);
            delivery.setShippingAddress(this.shippingAddress);
            delivery.setRecipientName(this.recipientName);
            delivery.setRecipientPhone(this.recipientPhone);
            delivery.setEstimatedDeliveryDate(this.estimatedDeliveryDate);
            delivery.setActualDeliveryDate(this.actualDeliveryDate);
            delivery.setTrackingNumber(this.trackingNumber);
            delivery.setDeliveryMethod(this.deliveryMethod);
            delivery.setDeliveryFee(this.deliveryFee);
            delivery.setDeliveryNotes(this.deliveryNotes);
            delivery.setItems(this.items);
            return delivery;
        }
    }
}
