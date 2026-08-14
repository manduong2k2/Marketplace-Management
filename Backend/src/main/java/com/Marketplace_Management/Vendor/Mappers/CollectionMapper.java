package com.Marketplace_Management.Vendor.Mappers;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Shared.Contracts.IMapper;
import com.Marketplace_Management.Vendor.Entities.CollectionEntity;
import com.Marketplace_Management.Vendor.Entities.VendorEntity;
import com.Marketplace_Management.Vendor.Models.Collection;

import jakarta.persistence.EntityManager;

@Component
public class CollectionMapper implements IMapper<Collection, CollectionEntity> {
    
    private final EntityManager entityManager;

    public CollectionMapper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Collection toDomain(CollectionEntity entity) {
        return Collection.builder()
                .id(entity.getId())
                .name(entity.getName())
                .displayOrder(entity.getDisplayOrder())
                .build();
    }
    
    @Override
    public CollectionEntity toEntity(Collection domain) {
        return new CollectionEntity(
            domain.getId(),
            domain.getName(),
            domain.getDisplayOrder(),
            entityManager.find(VendorEntity.class, domain.getVendorId())
        );
    }
}
