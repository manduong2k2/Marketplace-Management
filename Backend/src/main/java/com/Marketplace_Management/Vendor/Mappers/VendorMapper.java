package com.Marketplace_Management.Vendor.Mappers;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Shared.Contracts.EntityDomainMapper;
import com.Marketplace_Management.Vendor.Entities.VendorEntity;
import com.Marketplace_Management.Vendor.Models.Vendor;

@Component
public class VendorMapper implements EntityDomainMapper<Vendor,VendorEntity> {
    public Vendor toDomain(VendorEntity entity) {
        return Vendor.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .name(entity.getName())
            .status(entity.getStatus())
            .description(entity.getDescription())
            .logo(entity.getLogo())
            .banner(entity.getBanner())
            .taxCode(entity.getTaxCode())
            .email(entity.getEmail())
            .addressId(entity.getAddressId())
            .phone(entity.getPhone())
            .build();
    }

    public VendorEntity toEntity(Vendor model) {
        return VendorEntity.builder()
            .id(model.getId())
            .userId(model.getUserId())
            .name(model.getName())
            .status(model.getStatus())
            .description(model.getDescription())
            .logo(model.getLogo())
            .banner(model.getBanner())
            .taxCode(model.getTaxCode())
            .email(model.getEmail())
            .addressId(model.getAddressId())
            .phone(model.getPhone())
            .build();
    }
}
