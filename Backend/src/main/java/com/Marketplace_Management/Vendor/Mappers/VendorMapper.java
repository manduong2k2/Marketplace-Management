package com.Marketplace_Management.Vendor.Mappers;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Shared.Contracts.IMapper;
import com.Marketplace_Management.Vendor.Entities.VendorEntity;
import com.Marketplace_Management.Vendor.Models.Vendor;

@Component
public class VendorMapper implements IMapper<Vendor,VendorEntity> {
    public Vendor toDomain(VendorEntity entity) {
        return new Vendor(
            entity.getId(), entity.getUserId(), entity.getName(), entity.getStatus(),
            entity.getDescription(), entity.getLogo(), entity.getBanner(),
            entity.getTaxCode(), entity.getEmail(), entity.getAddressId(), entity.getPhone()
        );
    }

    public VendorEntity toEntity(Vendor model) {
        return new VendorEntity(
            model.getId(), model.getUserId(), model.getName(), model.getStatus(),
            model.getDescription(), model.getLogo(), model.getBanner(),
            model.getTaxCode(), model.getEmail(), model.getAddressId(), model.getPhone()
        );
    }
}
