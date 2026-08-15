package com.Marketplace_Management.Delivery.Models;

import java.util.UUID;
import com.Marketplace_Management.Shared.Models.AggregateRoot;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Address extends AggregateRoot<Long> {
    
    private UUID    userId;
    private String  title;
    private String  streetName;
    private String  houseNumber;
    private String  detail;
    private Ward    ward;
    private String  wardId;
    private Boolean isDefault;

    public Address() {
        super(null);
    }

    public Address(Long id) {
        super(id);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private UUID userId;
        private String title;
        private Ward ward;
        private String streetName;
        private String houseNumber;
        private String detail;
        private String wardId;
        private Boolean isDefault;
        
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        public Builder ward(Ward ward) {
            this.ward = ward;
            return this;
        }
        
        public Builder streetName(String streetName) {
            this.streetName = streetName;
            return this;
        }
        
        public Builder houseNumber(String houseNumber) {
            this.houseNumber = houseNumber;
            return this;
        }
        
        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }
        
        public Builder wardId(String wardId) {
            this.wardId = wardId;
            return this;
        }
        
        public Builder isDefault(Boolean isDefault) {
            this.isDefault = isDefault;
            return this;
        }
        
        public Address build() {
            Address address = new Address();
            address.setId(this.id);
            address.setUserId(this.userId);
            address.setTitle(this.title);
            address.setWard(this.ward);
            address.setStreetName(this.streetName);
            address.setHouseNumber(this.houseNumber);
            address.setDetail(this.detail);
            address.setWardId(this.wardId);
            address.setIsDefault(this.isDefault);
            return address;
        }
    }
}
