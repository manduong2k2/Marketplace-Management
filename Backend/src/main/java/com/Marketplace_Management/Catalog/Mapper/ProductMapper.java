package com.Marketplace_Management.Catalog.Mapper;

import java.util.HashMap;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Catalog.Entities.BrandEntity;
import com.Marketplace_Management.Catalog.Entities.CategoryEntity;
import com.Marketplace_Management.Catalog.Entities.ProductEntity;
import com.Marketplace_Management.Catalog.Entities.ProductOptionEntity;
import com.Marketplace_Management.Catalog.Entities.ProductVariantEntity;
import com.Marketplace_Management.Catalog.Models.Brand;
import com.Marketplace_Management.Catalog.Models.Category;
import com.Marketplace_Management.Catalog.Models.Product;
import com.Marketplace_Management.Catalog.Models.ProductOption;
import com.Marketplace_Management.Catalog.Models.ProductVariant;
import com.Marketplace_Management.Shared.Domain.Contracts.IMapper;

@Component
public class ProductMapper implements IMapper<Product, ProductEntity> {
    @PersistenceContext
    private EntityManager entityManager;

    private final IMapper<Brand, BrandEntity> brandMapper;
    private final IMapper<Category, CategoryEntity> categoryMapper;
    private final IMapper<ProductVariant, ProductVariantEntity> variantMapper;
    private final IMapper<ProductOption, ProductOptionEntity> optionMapper;

    public ProductMapper(IMapper<Brand, BrandEntity> brandMapper, IMapper<Category, CategoryEntity> categoryMapper,
                        IMapper<ProductVariant, ProductVariantEntity> variantMapper,
                        IMapper<ProductOption, ProductOptionEntity> optionMapper) {
        this.brandMapper = brandMapper;
        this.categoryMapper = categoryMapper;
        this.variantMapper = variantMapper;
        this.optionMapper = optionMapper;
    }

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
                            HashMap<String, String> options = new HashMap<>();
                            variant.getOptions().forEach(o -> options.put(o.getName(), o.getValue()));
                            variant.setOptions(optionEntities.stream().filter(o -> options.containsKey(o.getName()) && options.get(o.getName()).equals(o.getValue())).toList());
                        })
                        .collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList());
        return entity;
    }
}
