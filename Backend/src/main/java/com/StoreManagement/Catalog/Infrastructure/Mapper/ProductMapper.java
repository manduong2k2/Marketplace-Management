package com.StoreManagement.Catalog.Infrastructure.Mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.StoreManagement.Catalog.Domain.Models.Product;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.BrandEntity;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.CategoryEntity;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.ProductEntity;
import com.StoreManagement.Shared.Domain.File;
import com.StoreManagement.Shared.Domain.Contracts.IMapper;
import com.StoreManagement.Shared.Infrastructure.Persistence.Entity.FileEntity;

@Component
public class ProductMapper implements IMapper<Product, ProductEntity> {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public IMapper<File, FileEntity> fileMapper;

    @Override
    public Product toDomain(ProductEntity entity) {
        Product product = new Product();
        product.setId(entity.getId());
        product.setName(entity.getName());
        product.setDescription(entity.getDescription());
        product.setCode(entity.getCode());
        product.setPrice(entity.getPrice());
        product.setStock(entity.getStock());
        product.setBrandId(entity.getBrand().getId());
        product.setStatus(entity.getStatus());
        product.setCategoryIds(
                entity.getCategories() != null ? entity.getCategories().stream().map(category -> category.getId())
                        .collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList());
        product.setFiles(entity.getFiles() != null ? entity.getFiles().stream()
                .map(file -> fileMapper.toDomain(file))
                .collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList());
        return product;
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
        entity.setFiles(domain.getFiles() != null ? domain.getFiles().stream()
                .map(file -> entityManager.find(FileEntity.class, file.getId()))
                .collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList());
        entity.setStatus(domain.getStatus());
        entity.setCode(domain.getCode());
        entity.setPrice(domain.getPrice().getValue());
        entity.setStock(domain.getStock());

        return entity;
    }
}
