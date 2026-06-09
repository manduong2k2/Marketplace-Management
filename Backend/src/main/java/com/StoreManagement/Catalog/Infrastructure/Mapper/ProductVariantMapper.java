package com.StoreManagement.Catalog.Infrastructure.Mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.StoreManagement.Catalog.Domain.Models.ProductVariant;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.ProductVariantEntity;
import com.StoreManagement.Shared.Domain.File;
import com.StoreManagement.Shared.Domain.Contracts.IMapper;
import com.StoreManagement.Shared.Infrastructure.Persistence.Entity.FileEntity;

@Component
public class ProductVariantMapper implements IMapper<ProductVariant, ProductVariantEntity>{
    
    @Autowired
    private IMapper<File, FileEntity> fileMapper;

    public ProductVariant toDomain(ProductVariantEntity entity) {
        return new ProductVariant(
            entity.getId(),
            entity.getProduct().getId(),
            entity.getName(),
            entity.getCode(),
            entity.getPrice(),
            entity.getStock(),
            entity.getFiles() != null ? entity.getFiles().stream().map(fileMapper::toDomain).toList() : null
        );
    }
    
    public ProductVariantEntity toEntity(ProductVariant domain) {
        return new ProductVariantEntity(
            domain.getId(),
            domain.getName(),
            domain.getCode(),
            domain.getPrice().getValue(),
            domain.getStock(),
            domain.getFiles() != null ? domain.getFiles().stream().map(fileMapper::toEntity).toList() : null,
            null
        );
    }
    
}
