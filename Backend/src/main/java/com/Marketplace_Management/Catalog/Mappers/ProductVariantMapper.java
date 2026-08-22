package com.Marketplace_Management.Catalog.Mappers;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Catalog.Entities.ProductOptionEntity;
import com.Marketplace_Management.Catalog.Entities.ProductVariantEntity;
import com.Marketplace_Management.Catalog.Models.Product;
import com.Marketplace_Management.Catalog.Models.ProductOption;
import com.Marketplace_Management.Catalog.Models.ProductVariant;
import com.Marketplace_Management.Shared.Contracts.EntityDomainMapper;
import com.Marketplace_Management.Shared.Entities.FileEntity;
import com.Marketplace_Management.Shared.Models.File;

@Component
public class ProductVariantMapper implements EntityDomainMapper<ProductVariant, ProductVariantEntity>{

    private final EntityDomainMapper<File, FileEntity> fileMapper;
    private final EntityDomainMapper<ProductOption, ProductOptionEntity> optionMapper;

    public ProductVariantMapper(EntityDomainMapper<File, FileEntity> fileMapper, EntityDomainMapper<ProductOption, ProductOptionEntity> optionMapper) {
        this.fileMapper = fileMapper;
        this.optionMapper = optionMapper;
    }

    public ProductVariant toDomain(ProductVariantEntity entity) {

        if (entity == null) {
            return null;
        }
        
        return ProductVariant.builder()
            .id(entity.getId())
            .productId(entity.getProduct().getId())
            .name(entity.getName())
            .sku(entity.getSku())
            .price(entity.getPrice())
            .stock(entity.getStock())
            .optionList(entity.getOptionList())
            .images(entity.getImages() != null ? entity.getImages().stream().map(fileMapper::toDomain).toList() : null)
            .product(Product.builder()
                .id(entity.getProduct().getId())
                .name(entity.getProduct().getName())
                .description(entity.getProduct().getDescription())
                .brandId(entity.getProduct().getBrand().getId())
                .status(entity.getProduct().getStatus())
                .categoryIds(entity.getProduct().getCategories().stream().map(category -> category.getId()).toList())
                .build()
            )
            .options(entity.getOptions() != null ? entity.getOptions().stream().map(optionMapper::toDomain).collect(java.util.stream.Collectors.toSet()) : null)
            .build();
    }
    
    public ProductVariantEntity toEntity(ProductVariant domain) {

        if (domain == null) {
            return null;
        }
        
        return new ProductVariantEntity(
            domain.getId(),
            domain.getName(),
            domain.getSku(),
            domain.getPrice().getValue(),
            domain.getStock(),
            domain.getImages() != null ? domain.getImages().stream().map(fileMapper::toEntity).toList() : null,
            domain.getOptionList(),
            domain.getOptions() != null ? domain.getOptions().stream().map(optionMapper::toEntity).collect(java.util.stream.Collectors.toSet()) : null
        );
    }
}
