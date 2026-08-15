package com.Marketplace_Management.Delivery.Models;

import java.util.UUID;

import com.Marketplace_Management.Shared.Models.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DeliveryItem extends Entity<UUID> {

    private UUID deliveryId;
    private UUID orderItemId;
    private UUID productVariantId;
    private String productName;
    private String productSku;
    private int quantity;
    private int delivered;

    public DeliveryItem() {
        super(null);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private UUID deliveryId;
        private UUID orderItemId;
        private UUID productVariantId;
        private String productName;
        private String productSku;
        private int quantity;
        private int delivered;
        
        public Builder() {
        }
        
        public Builder deliveryId(UUID deliveryId) {
            this.deliveryId = deliveryId;
            return this;
        }
        
        public Builder orderItemId(UUID orderItemId) {
            this.orderItemId = orderItemId;
            return this;
        }
        
        public Builder productVariantId(UUID productVariantId) {
            this.productVariantId = productVariantId;
            return this;
        }
        
        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }
        
        public Builder productSku(String productSku) {
            this.productSku = productSku;
            return this;
        }
        
        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }
        
        public Builder delivered(int delivered) {
            this.delivered = delivered;
            return this;
        }
        
        public DeliveryItem build() {
            DeliveryItem deliveryItem = new DeliveryItem();
            deliveryItem.setDeliveryId(this.deliveryId);
            deliveryItem.setOrderItemId(this.orderItemId);
            deliveryItem.setProductVariantId(this.productVariantId);
            deliveryItem.setProductName(this.productName);
            deliveryItem.setProductSku(this.productSku);
            deliveryItem.setQuantity(this.quantity);
            deliveryItem.setDelivered(this.delivered);
            return deliveryItem;
        }
    }
}
