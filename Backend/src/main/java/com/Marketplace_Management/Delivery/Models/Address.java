package com.Marketplace_Management.Delivery.Models;

import java.util.UUID;
import com.Marketplace_Management.Shared.Models.AggregateRoot;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class Address extends AggregateRoot<Long> {
    
    private UUID    userId;
    private String  title;
    private String  streetName;
    private String  houseNumber;
    private String  detail;
    private Ward    ward;
    private String  wardId;
    private Boolean isDefault;

    public Address(Long id) {
        super(id);
    }
}
