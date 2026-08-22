package com.Marketplace_Management.Catalog.Repositories;

import jakarta.persistence.EntityManager;
import tools.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.Marketplace_Management.Catalog.Contracts.IProductRepository;
import com.Marketplace_Management.Catalog.DTOs.Commands.Product.GetListProductCommand;
import com.Marketplace_Management.Catalog.DTOs.Response.Short.ProductShortResponse;
import com.Marketplace_Management.Catalog.Entities.BrandEntity;
import com.Marketplace_Management.Catalog.Entities.CategoryEntity;
import com.Marketplace_Management.Catalog.Entities.ProductEntity;
import com.Marketplace_Management.Catalog.Entities.ProductOptionEntity;
import com.Marketplace_Management.Catalog.Entities.ProductVariantEntity;
import com.Marketplace_Management.Catalog.Models.Product;
import com.Marketplace_Management.Catalog.Models.ProductVariant;
import com.Marketplace_Management.Shared.Contracts.EntityDomainMapper;
import com.Marketplace_Management.Shared.DTOs.Responses.PaginatedResponse;
import com.Marketplace_Management.Shared.Entities.FileEntity;
import com.Marketplace_Management.Shared.Repositories.QueryBuilder.QueryBuilder;

import org.springframework.beans.factory.annotation.Value;

@Repository
public class ProductRepository implements IProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final EntityDomainMapper<Product, ProductEntity> productMapper;
    private final EntityDomainMapper<ProductVariant, ProductVariantEntity> variantMapper;
    private final EntityManager entityManager;
    private final ProductVariantJpaRepository productVariantJpaRepository;
    private final ObjectMapper objectMapper;

    @Value("${spring.application.base-url}")
    private String baseUrl;

    public ProductRepository(ProductJpaRepository jpaRepository,
            EntityDomainMapper<Product, ProductEntity> productMapper,
            EntityManager entityManager, ProductVariantJpaRepository productVariantJpaRepository,
            EntityDomainMapper<ProductVariant, ProductVariantEntity> variantMapper,
            ObjectMapper objectMapper) {
        this.jpaRepository = jpaRepository;
        this.productMapper = productMapper;
        this.entityManager = entityManager;
        this.productVariantJpaRepository = productVariantJpaRepository;
        this.variantMapper = variantMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public PaginatedResponse<ProductShortResponse> findAll(
            GetListProductCommand command) {

        QueryBuilder<ProductEntity> query = QueryBuilder.query(ProductEntity.class)
                .select("id", "name", "description")
                .with(List.of(
                        QueryBuilder.child(BrandEntity.class, "brand").select("id", "name"),
                        QueryBuilder.child(CategoryEntity.class, "categories").select("id", "name"),
                        QueryBuilder.child(ProductVariantEntity.class,"variants").select("id", "name", "stock", "price")
                                .with(List.of(
                                        QueryBuilder.child(ProductOptionEntity.class,"options")
                                                .select("id", "name", "value"),
                                        QueryBuilder.child(FileEntity.class,"images")
                                                .select("id", "url"))),
                        QueryBuilder.child(ProductOptionEntity.class, "options").select("id", "name", "value")
                    ));

        List<ProductShortResponse> responses = query
                .entityManager(entityManager)
                .objectMapper(objectMapper)
                .execute(ProductShortResponse.class);

        return new PaginatedResponse<>(
                responses,
                command.getPage(),
                command.getSize(),
                responses.size());
    }

    public Product save(Product Product) {
        ProductEntity productEntity = productMapper.toEntity(Product);
        ProductEntity saved = jpaRepository.save(productEntity);
        if (saved != null && saved.getVariants() != null && !saved.getVariants().isEmpty()) {
            saved.getVariants().forEach(
                    variant -> {
                        if (variant != null && variant.getImages() != null && !variant.getImages().isEmpty()) {
                            variant.getImages().forEach(image -> image.setEntityId(variant.getId()));
                        }

                        if (variant != null && variant.getOptions() != null && !variant.getOptions().isEmpty()) {
                            variant.setOptionList(variant.getOptions().stream()
                                    .map(option -> String.valueOf(option.getId())).collect(Collectors.joining(", ")));
                        }
                    });
            jpaRepository.save(productEntity);
        }

        return productMapper.toDomain(saved);
    }

    public Product findById(UUID id) {
        return jpaRepository.findById(id).map(productMapper::toDomain).orElse(null);
    }

    public Product update(Product Product) {
        ProductEntity productEntity = productMapper.toEntity(Product);
        ProductEntity updated = jpaRepository.save(productEntity);
        return productMapper.toDomain(updated);
    }

    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    public ProductVariant findVariantById(UUID variantId) {
        ProductVariantEntity entity = productVariantJpaRepository.findById(variantId).orElse(null);
        return entity != null ? variantMapper.toDomain(entity) : null;
    }
}