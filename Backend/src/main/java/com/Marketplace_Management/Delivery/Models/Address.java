package com.Marketplace_Management.Delivery.Models;

import java.util.UUID;

import com.Marketplace_Management.Shared.Models.AggregateRoot;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Address extends AggregateRoot<UUID> {
    private String wardCode;
    private String wardName;
    private String provinceName;
    private String streetName;
    private String houseNumber;
    private String detail;

    public Address() {
        super(null);
    }

    public Address(UUID id) {
        super(id);
    }
}
