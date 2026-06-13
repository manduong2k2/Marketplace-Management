package com.Marketplace_Management.Catalog.Repositories;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.Marketplace_Management.Catalog.Contracts.IProductRepository;
import com.Marketplace_Management.Catalog.DTO.Commands.Product.GetListProductCommand;
import com.Marketplace_Management.Catalog.DTO.Response.ProductOptionResponse;
import com.Marketplace_Management.Catalog.DTO.Response.Short.ProductShortResponse;
import com.Marketplace_Management.Catalog.DTO.Response.Short.ProductVariantShortResponse;
import com.Marketplace_Management.Catalog.Entities.ProductEntity;
import com.Marketplace_Management.Catalog.Entities.ProductOptionEntity;
import com.Marketplace_Management.Catalog.Entities.ProductVariantEntity;
import com.Marketplace_Management.Catalog.Entities.QCategoryEntity;
import com.Marketplace_Management.Catalog.Entities.QProductEntity;
import com.Marketplace_Management.Catalog.Entities.QProductOptionEntity;
import com.Marketplace_Management.Catalog.Entities.QProductVariantEntity;
import com.Marketplace_Management.Catalog.Models.Product;
import com.Marketplace_Management.Catalog.Models.ProductVariant;
import com.Marketplace_Management.Shared.Application.DTO.Responses.PaginatedResponse;
import com.Marketplace_Management.Shared.Domain.Contracts.IMapper;
import com.Marketplace_Management.Shared.Infrastructure.Persistence.Entity.FileEntity;
import com.Marketplace_Management.Shared.Infrastructure.Persistence.Entity.QFileEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Value;

@Repository
public class ProductRepository implements IProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final IMapper<Product, ProductEntity> productMapper;
    private final JPAQueryFactory queryFactory;

    @Value("${spring.application.base-url}")
    private String baseUrl;

    public ProductRepository(ProductJpaRepository jpaRepository, IMapper<Product, ProductEntity> productMapper,
            JPAQueryFactory queryFactory) {
        this.jpaRepository = jpaRepository;
        this.productMapper = productMapper;
        this.queryFactory = queryFactory;
    }

    @Override
    public PaginatedResponse<ProductShortResponse> findAll(GetListProductCommand command) {

        QProductEntity product = QProductEntity.productEntity;
        QProductVariantEntity variant = QProductVariantEntity.productVariantEntity;
        QProductOptionEntity option = QProductOptionEntity.productOptionEntity;
        QFileEntity file = QFileEntity.fileEntity;
        QCategoryEntity category = QCategoryEntity.categoryEntity;

        BooleanBuilder builder = new BooleanBuilder();

        if (command.getSearch() != null && !command.getSearch().isBlank()) {
            builder.and(product.name.containsIgnoreCase(command.getSearch()));
        }

        if (command.getBrandId() != null) {
            builder.and(product.brand.id.eq(command.getBrandId()));
        }

        if (command.getCategoryIds() != null && !command.getCategoryIds().isEmpty()) {
            builder.and(product.categories.any().id.in(command.getCategoryIds()));
        }

        long total = queryFactory
                .select(product.countDistinct())
                .from(product)
                .where(builder)
                .fetchOne();

        OrderSpecifier<?> orderSpecifier = "desc".equalsIgnoreCase(command.getSortOrder())
                ? product.name.desc()
                : product.name.asc();

        List<ProductEntity> products = queryFactory
                .selectFrom(product)
                .distinct()
                .leftJoin(product.categories, category)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset((long) command.getPage() * command.getSize())
                .limit(command.getSize())
                .fetch();

        if (products.isEmpty()) {
            return new PaginatedResponse<>(
                    List.of(),
                    command.getPage(),
                    command.getSize(),
                    0);
        }

        List<UUID> productIds = products.stream()
                .map(ProductEntity::getId)
                .toList();

        List<ProductOptionEntity> productOptions = queryFactory
                .selectFrom(option)
                .where(option.product.id.in(productIds))
                .fetch();

        Map<UUID, Set<ProductOptionResponse>> productOptionMap = productOptions.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getProduct().getId(),
                        Collectors.mapping(
                                o -> new ProductOptionResponse(
                                        o.getId(),
                                        o.getName(),
                                        o.getValue()),
                                Collectors.toSet())));

        List<ProductVariantEntity> variants = queryFactory
                .selectFrom(variant)
                .where(variant.product.id.in(productIds))
                .fetch();

        List<UUID> variantIds = variants.stream()
                .map(ProductVariantEntity::getId)
                .toList();

        List<FileEntity> files = variantIds.isEmpty()
                ? List.of()
                : queryFactory
                        .selectFrom(file)
                        .where(
                                file.entityType.eq("ProductVariant")
                                        .and(file.entityId.in(variantIds)))
                        .fetch();

        Map<UUID, Set<String>> imageMap = files.stream()
                .collect(Collectors.groupingBy(
                        FileEntity::getEntityId,
                        Collectors.mapping(
                                FileEntity::getUrl,
                                Collectors.toSet())));

        Map<UUID, Set<ProductOptionResponse>> variantOptionMap = variants.stream()
                .collect(Collectors.toMap(
                        ProductVariantEntity::getId,
                        v -> v.getOptions()
                                .stream()
                                .map(o -> new ProductOptionResponse(
                                        o.getId(),
                                        o.getName(),
                                        o.getValue()))
                                .collect(Collectors.toSet())));

        Map<UUID, List<ProductVariantShortResponse>> variantMap = variants.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getProduct().getId(),
                        Collectors.mapping(
                                v -> new ProductVariantShortResponse(
                                        v.getId(),
                                        v.getName(),
                                        v.getCode(),
                                        v.getStock(),
                                        v.getPrice(),
                                        v.getOptionList(),
                                        imageMap.getOrDefault(
                                                v.getId(),
                                                Set.of()),
                                        variantOptionMap.getOrDefault(
                                                v.getId(),
                                                Set.of()),
                                        baseUrl),
                                Collectors.toList())));

        List<ProductShortResponse> responses = products.stream()
                .map(p -> new ProductShortResponse(
                        p.getId(),
                        p.getName(),
                        variantMap.getOrDefault(
                                p.getId(),
                                List.of()),
                        productOptionMap.getOrDefault(
                                p.getId(),
                                Set.of())))
                .toList();

        return new PaginatedResponse<>(
                responses,
                command.getPage(),
                command.getSize(),
                total);
    }

    public Product save(Product Product) {
        ProductEntity productEntity = productMapper.toEntity(Product);
        ProductEntity saved = jpaRepository.save(productEntity);
        if (saved != null && saved.getVariants() != null && !saved.getVariants().isEmpty()) {
            saved.getVariants().forEach(
                    variant -> {
                        if (variant != null && variant.getFiles() != null && !variant.getFiles().isEmpty()) {
                            variant.getFiles().forEach(file -> file.setEntityId(variant.getId()));
                        }

                        if (variant != null && variant.getOptions() != null && !variant.getOptions().isEmpty()) {
                            variant.setOptionList(variant.getOptions().stream().map(option -> String.valueOf(option.getId())).collect(Collectors.joining(", ")));
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

    public ProductVariant findVariantById(UUID productId, UUID variantId) {
        var product = findById(productId);
        if (product == null) {
            return null;
        }
        return product.getVariants().stream()
                .filter(variant -> variant.getId().equals(variantId))
                .findFirst()
                .orElse(null);
    }
}