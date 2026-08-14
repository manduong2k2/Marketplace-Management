package com.Marketplace_Management.Vendor.Entities;

import java.util.UUID;

import com.Marketplace_Management.Shared.Entities.UuidEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "collections")
public class CollectionEntity extends UuidEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private Integer displayOrder;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private VendorEntity vendor;
    
    public CollectionEntity() {
    }
    
    public CollectionEntity(UUID id, String name, Integer displayOrder, VendorEntity vendor) {
        this.setId(id);
        this.name = name;
        this.displayOrder = displayOrder;
        this.vendor = vendor;
    }
}
