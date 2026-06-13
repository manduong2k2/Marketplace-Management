package com.Marketplace_Management.Catalog.Repositories;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.Marketplace_Management.Catalog.Contracts.IBrandRepository;
import com.Marketplace_Management.Catalog.DTO.Commands.Brand.GetListBrandCommand;
import com.Marketplace_Management.Catalog.Entities.BrandEntity;
import com.Marketplace_Management.Catalog.Models.Brand;
import com.Marketplace_Management.Shared.Application.DTO.Responses.PaginatedResponse;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.EntityManager;

@Repository
public class BrandRepository implements IBrandRepository {

    private final BrandJpaRepository jpaRepository;
    private final EntityManager entityManager;

    public BrandRepository(BrandJpaRepository jpaRepository, EntityManager entityManager) {
        this.jpaRepository = jpaRepository;
        this.entityManager = entityManager;
    }

    @Override
    public PaginatedResponse<Brand> findAll(GetListBrandCommand command) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BrandEntity> query = builder.createQuery(BrandEntity.class);

        Root<BrandEntity> root = query.from(BrandEntity.class);

        List<Predicate> predicates = new ArrayList<>();

        if (command.getSearch() != null && !command.getSearch().trim().isEmpty()) {
            String searchPattern = "%" + command.getSearch().toLowerCase() + "%";
            predicates.add(
                builder.or(
                    builder.like(builder.lower(root.get("name")), searchPattern),
                    builder.like(builder.lower(root.get("description")), searchPattern)
                )
            );
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
        Root<BrandEntity> countRoot = countQuery.from(BrandEntity.class);
        List<Predicate> countPredicates = new ArrayList<>();

        if (command.getSearch() != null && !command.getSearch().trim().isEmpty()) {
            String searchPattern = "%" + command.getSearch().toLowerCase() + "%";
            countPredicates.add(
                builder.or(
                    builder.like(builder.lower(countRoot.get("name")), searchPattern),
                    builder.like(builder.lower(countRoot.get("description")), searchPattern)
                )
            );
        }

        countQuery.where(countPredicates.toArray(new Predicate[0]));
        countQuery.select(builder.count(countRoot));
        Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

        // Pagination
        int firstResult = command.getPage() * command.getSize();
        int maxResults = command.getSize();

        List<BrandEntity> entities = entityManager.createQuery(query)
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .getResultList();

        List<Brand> brands = entities.stream()
                .map(this::toDomain)
                .toList();

        return new PaginatedResponse<>(brands, command.getPage(), command.getSize(), totalElements);
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
                entity.getDescription()
        );
    }

    private BrandEntity toEntity(Brand brand) {
        return new BrandEntity(
                brand.getId(),
                brand.getName(),
                brand.getImage(),
                brand.getDescription()
        );
    }
}