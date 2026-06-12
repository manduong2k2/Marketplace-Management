package com.Marketplace_Management.Vendor.Domain.Model;

import java.util.UUID;

import com.Marketplace_Management.Shared.Domain.AggregateRoot;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Vendor extends AggregateRoot<UUID> {
    private UUID userId;
    private String name;
    private VendorStatus status;
    private String description;
    private String logo;
    private String banner;
    private String taxCode;
    private String email;
    private UUID addressId;
    private String phone;

    public Vendor(UUID id, UUID userId, String name) {
        super(id);
        this.userId = userId;
        this.name = name;
        this.status = VendorStatus.PENDING;
    }

    public Vendor(UUID id, UUID userId, String name, VendorStatus status) {
        super(id);
        this.userId = userId;
        this.name = name;
        this.status = status;
    }

    public Vendor(UUID id, UUID userId, String name, VendorStatus status,
                  String description, String logo, String banner,
                  String taxCode, String email, UUID addressId, String phone) {
        super(id);
        this.userId = userId;
        this.name = name;
        this.status = status;
        this.description = description;
        this.logo = logo;
        this.banner = banner;
        this.taxCode = taxCode;
        this.email = email;
        this.addressId = addressId;
        this.phone = phone;
    }

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