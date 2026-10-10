package com.Marketplace_Management.Vendor.Models;

import java.util.UUID;

import com.Marketplace_Management.Shared.Models.AggregateRoot;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class Vendor extends AggregateRoot<UUID> {
    private UUID userId;
    private String name;
    @Builder.Default
    private VendorStatus status = VendorStatus.PENDING;
    private String description;
    private String logo;
    private String banner;
    private String taxCode;
    private String email;
    private UUID addressId;
    private String phone;




    public void activate() {
        if (this.status != VendorStatus.PENDING) {
            throw new IllegalStateException("Vendor must be PENDING to activate");
        }

        this.status = VendorStatus.ACTIVE;
    }

    public void ban() {
        this.status = VendorStatus.BANNED;
    }
}