package com.StoreManagement.Catalog.Infrastructure.Mapper;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.StoreManagement.Catalog.Domain.Models.Brand;
import com.StoreManagement.Catalog.Domain.Models.Category;
import com.StoreManagement.Catalog.Domain.Models.Product;
import com.StoreManagement.Catalog.Domain.Models.ProductOption;
import com.StoreManagement.Catalog.Domain.Models.ProductVariant;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.BrandEntity;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.CategoryEntity;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.ProductEntity;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.ProductOptionEntity;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.ProductVariantEntity;
import com.StoreManagement.Shared.Domain.Contracts.IMapper;

@Component
public class ProductMapper implements IMapper<Product, ProductEntity> {
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private IMapper<Brand, BrandEntity> brandMapper;

    @Autowired
    private IMapper<Category, CategoryEntity> categoryMapper;

    @Autowired
    private IMapper<ProductVariant, ProductVariantEntity> variantMapper;
    
    @Autowired
    private IMapper<ProductOption, ProductOptionEntity> optionMapper;

    @Override
    public Product toDomain(ProductEntity entity) {
        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .brandId(entity.getBrand().getId())
                .brand(brandMapper.toDomain(entity.getBrand()))
                .status(entity.getStatus())
                .categories(entity.getCategories() != null ? entity.getCategories().stream().map(category -> categoryMapper.toDomain(category))
                        .collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList())
                .categoryIds(
                        entity.getCategories() != null ? entity.getCategories().stream().map(category -> category.getId())
                                .collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList())
                .variants(entity.getVariants().stream().map(variantMapper::toDomain).toList())
                .options(entity.getOptions().stream().map(optionMapper::toDomain).toList())
                .build();
    }

    public ProductEntity toEntity(Product domain) {
        ProductEntity entity = new ProductEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setBrand(entityManager.find(BrandEntity.class, domain.getBrandId()));
        entity.setCategories(
                domain.getCategoryIds() != null ? domain.getCategoryIds().stream()
                        .map(categoryId -> entityManager.find(CategoryEntity.class, categoryId))
                        .collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList());
        entity.setStatus(domain.getStatus());
        
        List<ProductOptionEntity> optionEntities = domain.getOptions() != null ? domain.getOptions().stream().map(optionMapper::toEntity)
                        .peek(option -> option.setProduct(entity))
                        .collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList();
        entity.setOptions(optionEntities);

        entity.setVariants(
                domain.getVariants() != null ? domain.getVariants().stream().map(variantMapper::toEntity)
                        .peek(variant -> {
                            variant.setProduct(entity);
                            variant.setOptions(optionEntities);
                        })
                        .collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList());
        return entity;
    }
}
