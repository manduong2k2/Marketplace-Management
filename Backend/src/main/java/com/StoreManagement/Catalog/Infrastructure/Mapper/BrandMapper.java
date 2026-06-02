package com.StoreManagement.Catalog.Infrastructure.Mapper;

import org.springframework.stereotype.Component;

import com.StoreManagement.Catalog.Domain.Models.Brand;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.BrandEntity;
import com.StoreManagement.Shared.Domain.Contracts.IMapper;

@Component
public class BrandMapper implements IMapper<Brand, BrandEntity>{
    
    @Override
    public Brand toDomain(BrandEntity entity) {
        Brand brand = new Brand(entity.getId(), entity.getName(), entity.getImage(), entity.getDescription());
        return brand;
    }

    @Override
    public BrandEntity toEntity(Brand domain) {
        BrandEntity entity = new BrandEntity(domain.getId(), domain.getName(), domain.getImage(), domain.getDescription());
        return entity;
    }
    
}
