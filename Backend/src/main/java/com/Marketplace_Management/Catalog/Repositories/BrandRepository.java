package com.Marketplace_Management.Catalog.Repositories;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.Marketplace_Management.Catalog.Contracts.IBrandRepository;
import com.Marketplace_Management.Catalog.DTOs.Commands.Brand.GetListBrandCommand;
import com.Marketplace_Management.Catalog.DTOs.Response.BrandResponse;
import com.Marketplace_Management.Catalog.Entities.BrandEntity;
import com.Marketplace_Management.Catalog.Entities.ProductEntity;
import com.Marketplace_Management.Catalog.Models.Brand;
import com.Marketplace_Management.Shared.DTOs.Responses.PaginatedResponse;
import com.Marketplace_Management.Shared.Utils.QueryBuilder.EntityMetadataRegistry;
import com.Marketplace_Management.Shared.Utils.QueryBuilder.QueryBuilder;

import tools.jackson.databind.ObjectMapper;

@Repository
public class BrandRepository implements IBrandRepository {

    private final BrandJpaRepository jpaRepository;
    private final DSLContext dslContext;
    private final EntityMetadataRegistry metadataRegistry;
    private final ObjectMapper objectMapper;

    public BrandRepository(BrandJpaRepository jpaRepository, DSLContext dslContext,
            EntityMetadataRegistry metadataRegistry, ObjectMapper objectMapper) {
        this.jpaRepository = jpaRepository;
        this.dslContext = dslContext;
        this.metadataRegistry = metadataRegistry;
        this.objectMapper = objectMapper;
    }

    @Override
    public PaginatedResponse<BrandResponse> findAll(GetListBrandCommand command) {
        QueryBuilder<BrandEntity> queryBuilder = new QueryBuilder<>(dslContext, metadataRegistry, objectMapper);
        queryBuilder.query(BrandEntity.class)
                .select("id", "name", "description");

        QueryBuilder<ProductEntity> productQueryBuilder = new QueryBuilder<>(dslContext, metadataRegistry,
                objectMapper);

        long total = queryBuilder.count();

        int offset = command.getPage() * command.getSize();
        var data = queryBuilder.get(command.getSize(), offset)
                .stream()
                .map(item -> {
                    BrandResponse response = queryBuilder.to(item, BrandResponse.class);
                    long productCount = productQueryBuilder.query(ProductEntity.class)
                            .select("id", "name")
                            .with("brand", brand -> {
                                brand.select("id", "name");
                            })
                            .where("brand.id", "=", item.get("id"))
                            .count();
                    response.setProductCount(productCount);
                    return response;
                })
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                data,
                command.getPage(),
                command.getSize(),
                total);
    }

    @Override
    public Brand save(Brand brand) {
        BrandEntity entity = toEntity(brand);
        BrandEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Brand> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<Brand> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(this::toDomain);
    }

    @Override
    public Brand update(Brand brand) {
        BrandEntity entity = toEntity(brand);
        BrandEntity updated = jpaRepository.save(entity);
        return toDomain(updated);
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    // ===== mapping =====

    private Brand toDomain(BrandEntity entity) {
        return new Brand(
                entity.getId(),
                entity.getName(),
                entity.getImage(),
                entity.getDescription());
    }

    private BrandEntity toEntity(Brand brand) {
        return new BrandEntity(
                brand.getId(),
                brand.getName(),
                brand.getImage(),
                brand.getDescription());
    }
}