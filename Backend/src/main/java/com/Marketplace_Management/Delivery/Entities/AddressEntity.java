package com.Marketplace_Management.Delivery.Entities;

import java.util.UUID;

import org.hibernate.annotations.Nationalized;

import com.Marketplace_Management.Shared.Entities.NumericEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "addresses", indexes = {
        @Index(name = "idx_address_ward", columnList = "wardId")
})
public class AddressEntity extends NumericEntity {
    private UUID userId;

    @Nationalized
    private String title;

    @Nationalized
    @NotNull
    private String streetName;
    
    @Nationalized
    @NotNull
    private String houseNumber;
    
    @Nationalized
    private String detail;
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "ward_id", nullable = false)
    private WardEntity ward;
}
