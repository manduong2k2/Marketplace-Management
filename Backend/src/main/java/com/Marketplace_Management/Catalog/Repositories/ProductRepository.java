package com.Marketplace_Management.Catalog.Repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.Marketplace_Management.Catalog.Contracts.IProductRepository;
import com.Marketplace_Management.Catalog.DTOs.Commands.Product.GetListProductCommand;
import com.Marketplace_Management.Catalog.DTOs.Response.ProductOptionResponse;
import com.Marketplace_Management.Catalog.DTOs.Response.Short.ProductShortResponse;
import com.Marketplace_Management.Catalog.DTOs.Response.Short.ProductVariantShortResponse;
import com.Marketplace_Management.Catalog.Entities.ProductEntity;
import com.Marketplace_Management.Catalog.Entities.ProductVariantEntity;
import com.Marketplace_Management.Catalog.Models.Product;
import com.Marketplace_Management.Catalog.Models.ProductVariant;
import com.Marketplace_Management.Shared.Contracts.IMapper;
import com.Marketplace_Management.Shared.DTOs.Responses.PaginatedResponse;
import org.springframework.beans.factory.annotation.Value;

@Repository
public class ProductRepository implements IProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final IMapper<Product, ProductEntity> productMapper;
    private final IMapper<ProductVariant, ProductVariantEntity> variantMapper;
    private final EntityManager entityManager;
    private final ProductVariantJpaRepository productVariantJpaRepository;

    @Value("${spring.application.base-url}")
    private String baseUrl;

    public ProductRepository(ProductJpaRepository jpaRepository, IMapper<Product, ProductEntity> productMapper,
            EntityManager entityManager, ProductVariantJpaRepository productVariantJpaRepository,
            IMapper<ProductVariant, ProductVariantEntity> variantMapper) {
        this.jpaRepository = jpaRepository;
        this.productMapper = productMapper;
        this.entityManager = entityManager;
        this.productVariantJpaRepository = productVariantJpaRepository;
        this.variantMapper = variantMapper;
    }

    @Override
    public PaginatedResponse<ProductShortResponse> findAll(GetListProductCommand command) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT DISTINCT p FROM ProductEntity p ");
        jpql.append("LEFT JOIN FETCH p.variants v ");
        jpql.append("LEFT JOIN FETCH v.options o ");
        jpql.append("LEFT JOIN FETCH v.files f ");
        jpql.append("LEFT JOIN FETCH p.categories c ");
        jpql.append("WHERE 1=1 ");

        if (command.getSearch() != null && !command.getSearch().isBlank()) {
            jpql.append("AND LOWER(p.name) LIKE LOWER(:search) ");
        }

        if (command.getBrandId() != null) {
            jpql.append("AND p.brand.id = :brandId ");
        }

        if (command.getCategoryIds() != null && !command.getCategoryIds().isEmpty()) {
            jpql.append("AND c.id IN :categoryIds ");
        }

        if (command.getVendorId() != null) {
            jpql.append("AND p.vendorId = :vendorId ");
        }

        String countJpql = "SELECT COUNT(DISTINCT p.id) FROM ProductEntity p " +
                "LEFT JOIN p.categories c WHERE 1=1 ";

        if (command.getSearch() != null && !command.getSearch().isBlank()) {
            countJpql += "AND LOWER(p.name) LIKE LOWER(:search) ";
        }

        if (command.getBrandId() != null) {
            countJpql += "AND p.brand.id = :brandId ";
        }

        if (command.getCategoryIds() != null && !command.getCategoryIds().isEmpty()) {
            countJpql += "AND c.id IN :categoryIds ";
        }

        if (command.getVendorId() != null) {
            countJpql += "AND p.vendorId = :vendorId ";
        }

        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);

        if (command.getSearch() != null && !command.getSearch().isBlank()) {
            countQuery.setParameter("search", "%" + command.getSearch() + "%");
        }
        if (command.getBrandId() != null) {
            countQuery.setParameter("brandId", command.getBrandId());
        }
        if (command.getCategoryIds() != null && !command.getCategoryIds().isEmpty()) {
            countQuery.setParameter("categoryIds", command.getCategoryIds());
        }
        if (command.getVendorId() != null) {
            countQuery.setParameter("vendorId", command.getVendorId());
        }

        long total = countQuery.getSingleResult();

        jpql.append("ORDER BY p.name ").append(command.getSortOrder().equalsIgnoreCase("desc") ? "DESC" : "ASC");

        TypedQuery<ProductEntity> query = entityManager.createQuery(jpql.toString(), ProductEntity.class);
        query.setFirstResult(command.getPage() * command.getSize());
        query.setMaxResults(command.getSize());

        if (command.getSearch() != null && !command.getSearch().isBlank()) {
            query.setParameter("search", "%" + command.getSearch() + "%");
        }
        if (command.getBrandId() != null) {
            query.setParameter("brandId", command.getBrandId());
        }
        if (command.getCategoryIds() != null && !command.getCategoryIds().isEmpty()) {
            query.setParameter("categoryIds", command.getCategoryIds());
        }
        if (command.getVendorId() != null) {
            query.setParameter("vendorId", command.getVendorId());
        }

        List<ProductEntity> products = query.getResultList();

        List<ProductShortResponse> responses = products.stream()
                .map(p -> {
                    List<ProductVariantShortResponse> variantResponses = p.getVariants().stream()
                            .map(v -> {
                                Set<String> images = v.getFiles() != null
                                        ? v.getFiles().stream()
                                                .map(f -> baseUrl + "/" + f.getUrl())
                                                .collect(Collectors.toSet())
                                        : Set.of();

                                Set<ProductOptionResponse> optionResponses = v.getOptions() != null
                                        ? v.getOptions().stream()
                                                .map(o -> new ProductOptionResponse(o.getId(), o.getName(), o.getValue()))
                                                .collect(Collectors.toSet())
                                        : Set.of();

                                return new ProductVariantShortResponse(
                                        v.getId(),
                                        v.getName(),
                                        v.getSku(),
                                        v.getStock(),
                                        v.getPrice(),
                                        v.getOptionList(),
                                        images,
                                        optionResponses,
                                        baseUrl);
                            })
                            .toList();

                    Set<ProductOptionResponse> productOptionResponses = p.getOptions() != null
                            ? p.getOptions().stream()
                                    .map(o -> new ProductOptionResponse(o.getId(), o.getName(), o.getValue()))
                                    .collect(Collectors.toSet())
                            : Set.of();

                    return new ProductShortResponse(p.getId(), p.getName(), variantResponses, productOptionResponses);
                })
                .toList();

        return new PaginatedResponse<>(responses, command.getPage(), command.getSize(), total);
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

    public ProductVariant findVariantById(UUID variantId) {
        ProductVariantEntity entity = productVariantJpaRepository.findById(variantId).orElse(null);
        return entity != null ? variantMapper.toDomain(entity) : null;
    }
}