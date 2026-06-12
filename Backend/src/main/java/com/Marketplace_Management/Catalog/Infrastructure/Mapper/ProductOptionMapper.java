package com.Marketplace_Management.Catalog.Infrastructure.Mapper;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Catalog.Domain.Models.Product;
import com.Marketplace_Management.Catalog.Domain.Models.ProductOption;
import com.Marketplace_Management.Catalog.Infrastructure.Persistence.Entity.ProductOptionEntity;
import com.Marketplace_Management.Shared.Domain.Contracts.IMapper;

@Component
public class ProductOptionMapper implements IMapper<ProductOption, ProductOptionEntity> {

    public ProductOptionEntity toEntity(ProductOption domain) {
        return new ProductOptionEntity(
            domain.getId() != null ? domain.getId() : null,
            domain.getName(),
            domain.getValue(),
            null
        );
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
