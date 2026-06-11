package com.StoreManagement.Catalog.Infrastructure.Mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.StoreManagement.Catalog.Domain.Models.Product;
import com.StoreManagement.Catalog.Domain.Models.ProductOption;
import com.StoreManagement.Catalog.Domain.Models.ProductVariant;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.ProductOptionEntity;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.ProductVariantEntity;
import com.StoreManagement.Shared.Domain.File;
import com.StoreManagement.Shared.Domain.Contracts.IMapper;
import com.StoreManagement.Shared.Infrastructure.Persistence.Entity.FileEntity;

@Component
public class ProductVariantMapper implements IMapper<ProductVariant, ProductVariantEntity>{
    
    @Autowired
    private IMapper<File, FileEntity> fileMapper;

    @Autowired
    private IMapper<ProductOption, ProductOptionEntity> optionMapper;

    public ProductVariant toDomain(ProductVariantEntity entity) {

        if (entity == null) {
            return null;
        }
        
        return ProductVariant.builder()
            .id(entity.getId())
            .productId(entity.getProduct().getId())
            .name(entity.getName())
            .code(entity.getCode())
            .price(entity.getPrice())
            .stock(entity.getStock())
            .files(entity.getFiles() != null ? entity.getFiles().stream().map(fileMapper::toDomain).toList() : null)
            .product(Product.builder()
                .id(entity.getProduct().getId())
                .name(entity.getProduct().getName())
                .description(entity.getProduct().getDescription())
                .brandId(entity.getProduct().getBrand().getId())
                .status(entity.getProduct().getStatus())
                .categoryIds(entity.getProduct().getCategories().stream().map(category -> category.getId()).toList())
                .build()
            )
            .options(entity.getOptions() != null ? entity.getOptions().stream().map(optionMapper::toDomain).toList() : null)
            .build();
    }
    
    public ProductVariantEntity toEntity(ProductVariant domain) {

        if (domain == null) {
            return null;
        }
        
        return new ProductVariantEntity(
            domain.getId(),
            domain.getName(),
            domain.getCode(),
            domain.getPrice().getValue(),
            domain.getStock(),
            domain.getFiles() != null ? domain.getFiles().stream().map(fileMapper::toEntity).toList() : null,
            null,
            domain.getOptions() != null ? domain.getOptions().stream().map(optionMapper::toEntity).toList() : null
        );
    }
}
