package com.StoreManagement.Catalog.Infrastructure.Persistence.Repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import com.StoreManagement.Catalog.Application.DTO.Commands.Product.GetListProductCommand;
import com.StoreManagement.Catalog.Domain.Contract.IProductRepository;
import com.StoreManagement.Catalog.Domain.Models.Product;
import com.StoreManagement.Catalog.Domain.Models.ProductVariant;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.ProductEntity;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.QProductEntity;
import com.StoreManagement.Shared.Application.DTO.Responses.PaginatedResponse;
import com.StoreManagement.Shared.Domain.Contracts.IMapper;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class ProductRepository implements IProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final IMapper<Product, ProductEntity> productMapper;
    private final JPAQueryFactory jpaQueryFactory;

    public ProductRepository(ProductJpaRepository jpaRepository, IMapper<Product, ProductEntity> productMapper, JPAQueryFactory jpaQueryFactory) {
        this.jpaRepository = jpaRepository;
        this.productMapper = productMapper;
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public PaginatedResponse<Product> findAll(GetListProductCommand command) {

        QProductEntity product = QProductEntity.productEntity;
        String sortBy = command.getSortBy().isBlank() ? "updated_at" : command.getSortBy();
        String sortOrder = command.getSortOrder().equals("asc") ? "asc" : "desc";
        PathBuilder<ProductEntity> entityPath = new PathBuilder<>(ProductEntity.class, product.getMetadata());
        ComparablePath<?> path = entityPath.getComparable(
                sortBy,
                Comparable.class);

        List<ProductEntity> products = jpaQueryFactory
                .selectFrom(product)
                .leftJoin(product.brand).fetchJoin()
                .leftJoin(product.categories).fetchJoin()
                .where(
                        command.getSearch().isBlank() ? null
                                : product.name.containsIgnoreCase(command.getSearch())
                                        .or(product.brand.name.containsIgnoreCase(command.getSearch())),

                        command.getBrandId() == null ? null : product.brand.id.eq(command.getBrandId()),

                        command.getCategoryIds().isEmpty() ? null
                                : product.categories.any().id.in(command.getCategoryIds()))
                .offset((long) command.getPage() * command.getSize())
                .limit(command.getSize())
                .orderBy(sortOrder.equals("asc") ? path.asc() : path.desc())
                .fetch();

        Long total = jpaQueryFactory
                .select(product.count())
                .from(product)
                .where(
                        command.getSearch().isBlank() ? null
                                : product.name.containsIgnoreCase(command.getSearch())
                                        .or(product.brand.name.containsIgnoreCase(command.getSearch())),

                        command.getBrandId() == null ? null : product.brand.id.eq(command.getBrandId()),

                        command.getCategoryIds().isEmpty() ? null
                                : product.categories.any().id.in(command.getCategoryIds()))
                .fetchOne();

        return new PaginatedResponse<Product>(
                products.stream().map(productMapper::toDomain).toList(),
                command.getPage(),
                command.getSize(),
                total != null ? total : 0);
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
                    });
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