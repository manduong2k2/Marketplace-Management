package com.Marketplace_Management.Vendor.Models;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collection {
    private UUID id;
    private UUID vendorId;
    private String name;
    private Integer displayOrder;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID vendorId;
        private String name;
        private Integer displayOrder;
        
        public Builder() {
        }
        
        public Builder id(UUID id) {
            this.id = id;
            return this;
        }
        
        public Builder vendorId(UUID vendorId) {
            this.vendorId = vendorId;
            return this;
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder displayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
            return this;
        }
        
        public Collection build() {
            Collection collection = new Collection();
            collection.setId(this.id);
            collection.setVendorId(this.vendorId);
            collection.setName(this.name);
            collection.setDisplayOrder(this.displayOrder);
            return collection;
        }
    }
}
