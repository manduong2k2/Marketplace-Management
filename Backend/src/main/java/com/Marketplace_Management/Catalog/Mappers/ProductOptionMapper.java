package com.Marketplace_Management.Catalog.Mappers;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Catalog.Entities.ProductOptionEntity;
import com.Marketplace_Management.Catalog.Models.Product;
import com.Marketplace_Management.Catalog.Models.ProductOption;
import com.Marketplace_Management.Shared.Contracts.EntityDomainMapper;

@Component
public class ProductOptionMapper implements EntityDomainMapper<ProductOption, ProductOptionEntity> {

    public ProductOptionEntity toEntity(ProductOption domain) {
        return ProductOptionEntity.builder()
            .id(domain.getId())
            .name(domain.getName())
            .value(domain.getValue())
            .build();
    }

    public ProductOption toDomain(ProductOptionEntity entity) {
        return ProductOption.builder()
            .id(entity.getId())
            .name(entity.getName())
            .value(entity.getValue())
            .product(entity.getProduct() != null ? Product.builder()
                .id(entity.getProduct().getId())
                .name(entity.getProduct().getName())
                .description(entity.getProduct().getDescription())
                .build() : null)
            .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
            .build();
    }

}
