package com.StoreManagement.Catalog.Infrastructure.Persistence.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.StoreManagement.Catalog.Application.DTO.Commands.Product.GetListProductCommand;
import com.StoreManagement.Catalog.Application.DTO.Response.PaginatedResponse;
import com.StoreManagement.Catalog.Domain.Contract.IProductRepository;
import com.StoreManagement.Catalog.Domain.Models.Product;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.CategoryEntity;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.ProductEntity;
import com.StoreManagement.Shared.Domain.Contracts.IMapper;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.EntityManager;

@Repository
public class ProductRepository implements IProductRepository {
    private final ProductJpaRepository jpaRepository;
    private final IMapper<Product, ProductEntity> productMapper;

    @Autowired
    private EntityManager entityManager;

    public ProductRepository(ProductJpaRepository jpaRepository, IMapper<Product, ProductEntity> productMapper,
            EntityManager entityManager) {
        this.jpaRepository = jpaRepository;
        this.productMapper = productMapper;
        this.entityManager = entityManager;
    }

    public PaginatedResponse<Product> findAll(GetListProductCommand command) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductEntity> query = builder.createQuery(ProductEntity.class);

        Root<ProductEntity> root = query.from(ProductEntity.class);

        List<Predicate> predicates = new ArrayList<>();

        if (command.getCategoryIds() != null
                && !command.getCategoryIds().isEmpty()) {

            Join<ProductEntity, CategoryEntity> categoryJoin = root.join("categories");

            predicates.add(
                    categoryJoin.get("id")
                            .in(command.getCategoryIds()));

            query.distinct(true);
        }

        if (command.getSearch() != null && !command.getSearch().trim().isEmpty()) {
            String searchPattern = "%" + command.getSearch().toLowerCase() + "%";
            predicates.add(
                builder.or(
                    builder.like(builder.lower(root.get("name")), searchPattern),
                    builder.like(builder.lower(root.get("description")), searchPattern)
                )
            );
        }

        if (command.getBrandId() != null) {
            predicates.add(builder.equal(root.get("brand").get("id"), command.getBrandId()));
        }

        query.where(predicates.toArray(new Predicate[0]));

        // Sorting
        if (command.getSortBy() != null && !command.getSortBy().trim().isEmpty()) {
            jakarta.persistence.criteria.Path<Object> sortPath = root.get(command.getSortBy());
            Order order;
            if ("desc".equalsIgnoreCase(command.getSortOrder())) {
                order = builder.desc(sortPath);
            } else {
                order = builder.asc(sortPath);
            }
            query.orderBy(order);
        }

        // Get total count
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<ProductEntity> countRoot = countQuery.from(ProductEntity.class);
        List<Predicate> countPredicates = new ArrayList<>();

        if (command.getCategoryIds() != null
                && !command.getCategoryIds().isEmpty()) {
            Join<ProductEntity, CategoryEntity> categoryJoin = countRoot.join("categories");
            countPredicates.add(
                    categoryJoin.get("id")
                            .in(command.getCategoryIds()));
            countQuery.distinct(true);
        }

        if (command.getSearch() != null && !command.getSearch().trim().isEmpty()) {
            String searchPattern = "%" + command.getSearch().toLowerCase() + "%";
            countPredicates.add(
                builder.or(
                    builder.like(builder.lower(countRoot.get("name")), searchPattern),
                    builder.like(builder.lower(countRoot.get("description")), searchPattern)
                )
            );
        }

        if (command.getBrandId() != null) {
            countPredicates.add(builder.equal(countRoot.get("brand").get("id"), command.getBrandId()));
        }

        countQuery.where(countPredicates.toArray(new Predicate[0]));
        countQuery.select(builder.count(countRoot));
        Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

        // Pagination
        int firstResult = command.getPage() * command.getSize();
        int maxResults = command.getSize();

        List<ProductEntity> entities = entityManager.createQuery(query)
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .getResultList();

        List<Product> products = entities.stream()
                .map(productMapper::toDomain)
                .toList();

        return new PaginatedResponse<>(products, command.getPage(), command.getSize(), totalElements);
    }

    @Override
    public Product save(Product Product) {
        ProductEntity productEntity = productMapper.toEntity(Product);
        ProductEntity saved = jpaRepository.save(productEntity);
        return productMapper.toDomain(saved);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaRepository.findById(id).map(productMapper::toDomain);
    }

    @Override
    public List<Product> findByName(String name) {
        return jpaRepository.findByName(name).stream()
                .map(productMapper::toDomain)
                .toList();
    }

    @Override
    public Product update(Product Product) {
        ProductEntity productEntity = productMapper.toEntity(Product);
        ProductEntity updated = jpaRepository.save(productEntity);
        return productMapper.toDomain(updated);
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}