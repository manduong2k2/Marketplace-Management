package com.Marketplace_Management.Catalog.Mappers;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Catalog.Entities.BrandEntity;
import com.Marketplace_Management.Catalog.Models.Brand;
import com.Marketplace_Management.Shared.Contracts.EntityDomainMapper;

@Component
public class BrandMapper implements EntityDomainMapper<Brand, BrandEntity>{
    
    @Override
    public Brand toDomain(BrandEntity entity) {
        return Brand.builder()
                .id(entity.getId())
                .name(entity.getName())
                .image(entity.getImage())
                .description(entity.getDescription())
                .build();
    }

    @Override
    public BrandEntity toEntity(Brand domain) {
        return BrandEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .image(domain.getImage())
                .description(domain.getDescription())
                .build();
    }
    
}
