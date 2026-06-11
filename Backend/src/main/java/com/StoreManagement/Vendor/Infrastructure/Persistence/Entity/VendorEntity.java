package com.StoreManagement.Vendor.Infrastructure.Persistence.Entity;

import java.util.UUID;

import org.hibernate.annotations.Nationalized;
import com.StoreManagement.Shared.Infrastructure.Persistence.UuidEntity;
import com.StoreManagement.Vendor.Domain.Model.VendorStatus;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "vendors")
@Data
@EqualsAndHashCode(callSuper = false)
public class VendorEntity extends UuidEntity {
    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VendorStatus status;

    @Column(length = 500)
    @Nationalized
    private String description;

    @Column(length = 500)
    private String logo;

    @Column(length = 500)
    private String banner;

    @Column(length = 100, unique = true)
    private String taxCode;

    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 100, unique = true)
    private UUID addressId;

    @Column(length = 15, unique = true)
    private String phone;

    public VendorEntity() {}

    public VendorEntity(UUID id, UUID userId, String name, VendorStatus status,
                        String description, String logo, String banner,
                        String taxCode, String email, UUID addressId, String phone) {
        this.setId(id);
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
}