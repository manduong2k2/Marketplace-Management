package com.StoreManagement.Vendor.Infrastructure.Mapper;

import org.springframework.stereotype.Component;

import com.StoreManagement.Shared.Domain.Contracts.IMapper;
import com.StoreManagement.Vendor.Domain.Model.Vendor;
import com.StoreManagement.Vendor.Infrastructure.Persistence.Entity.VendorEntity;

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
