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
import com.Marketplace_Management.Catalog.Mappers.BrandMapper;
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
    private final BrandMapper brandMapper;

    public BrandRepository(BrandJpaRepository jpaRepository, DSLContext dslContext,
            EntityMetadataRegistry metadataRegistry, ObjectMapper objectMapper, BrandMapper brandMapper) {
        this.jpaRepository = jpaRepository;
        this.dslContext = dslContext;
        this.metadataRegistry = metadataRegistry;
        this.objectMapper = objectMapper;
        this.brandMapper = brandMapper;
    }

    @Override
    public PaginatedResponse<BrandResponse> findAll(GetListBrandCommand command) {
        QueryBuilder<BrandEntity> queryBuilder = new QueryBuilder<>(dslContext, metadataRegistry, objectMapper);
        queryBuilder.query(BrandEntity.class)
                .withCount("products", product -> {
                    product.select("id", "name");
                })
                .when(command.getSearch() != null && !command.getSearch().isEmpty(), q -> {
                    q.where("name", "ILIKE", "%" + command.getSearch() + "%");
                })
                .select("id", "name", "description", "image");

        long total = queryBuilder.count();

        int offset = command.getPage() * command.getSize();
        var data = queryBuilder.get(command.getSize(), offset)
                .stream()
                .map(item -> queryBuilder.to(item, BrandResponse.class))
                .collect(Collectors.toList());

        return PaginatedResponse.of(
                data,
                command.getPage(),
                command.getSize(),
                total);
    }

    @Override
    public Brand save(Brand brand) {
        BrandEntity entity = brandMapper.toEntity(brand);
        BrandEntity saved = jpaRepository.save(entity);
        return brandMapper.toDomain(saved);
    }

    @Override
    public Optional<Brand> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(brandMapper::toDomain);
    }

    @Override
    public Optional<Brand> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(brandMapper::toDomain);
    }

    @Override
    public Brand update(Brand brand) {
        BrandEntity entity = brandMapper.toEntity(brand);
        BrandEntity updated = jpaRepository.save(entity);
        return brandMapper.toDomain(updated);
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

}
