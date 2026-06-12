package com.Marketplace_Management.Catalog.Infrastructure.Persistence.Repository;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.Marketplace_Management.Catalog.Application.DTO.Commands.Category.GetListCategoryCommand;
import com.Marketplace_Management.Catalog.Domain.Contract.ICategoryRepository;
import com.Marketplace_Management.Catalog.Domain.Models.Category;
import com.Marketplace_Management.Catalog.Infrastructure.Persistence.Entity.CategoryEntity;
import com.Marketplace_Management.Shared.Application.DTO.Responses.PaginatedResponse;
import com.Marketplace_Management.Shared.Domain.Contracts.IMapper;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.EntityManager;

@Repository
public class CategoryRepository implements ICategoryRepository {

    private final CategoryJpaRepository jpaRepository;
    private final IMapper<Category, CategoryEntity> mapper;
    private final EntityManager entityManager;

    public CategoryRepository(CategoryJpaRepository jpaRepository, IMapper<Category, CategoryEntity> mapper, EntityManager entityManager) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.entityManager = entityManager;
    }

    @Override
    public PaginatedResponse<Category> findAll(GetListCategoryCommand command) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CategoryEntity> query = builder.createQuery(CategoryEntity.class);

        Root<CategoryEntity> root = query.from(CategoryEntity.class);

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
        Root<CategoryEntity> countRoot = countQuery.from(CategoryEntity.class);
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

        List<CategoryEntity> entities = entityManager.createQuery(query)
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .getResultList();

        List<Category> categories = entities.stream()
                .map(mapper::toDomain)
                .toList();

        return new PaginatedResponse<>(categories, command.getPage(), command.getSize(), totalElements);
    }

    @Override
    public Category save(Category category) {
        CategoryEntity entity = mapper.toEntity(category);
        CategoryEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(mapper::toDomain);
    }
    
    @Override
    public Category update(Category Category) {
        CategoryEntity entity = mapper.toEntity(Category);
        CategoryEntity updated = jpaRepository.save(entity);
        return mapper.toDomain(updated);
    }
    
    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}