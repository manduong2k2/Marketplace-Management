package com.Marketplace_Management.Catalog.Mappers;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Catalog.Entities.CategoryEntity;
import com.Marketplace_Management.Catalog.Models.Category;
import com.Marketplace_Management.Shared.Contracts.EntityDomainMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class CategoryMapper implements EntityDomainMapper<Category, CategoryEntity>{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Category toDomain(CategoryEntity entity) {
        Category parent = null;
        if (entity.getParent() != null) {
            parent = Category.builder()
                .id(entity.getParent().getId())
                .name(entity.getParent().getName())
                .image(entity.getParent().getImage())
                .description(entity.getParent().getDescription())
                .build();
        }

        return Category.builder()
            .id(entity.getId())
            .name(entity.getName())
            .image(entity.getImage())
            .description(entity.getDescription())
            .parent(parent)
            .children(entity.getChildren().isEmpty() ? java.util.List.of() : entity.getChildren().stream().map(this::toDomain).toList())
            .build();
    }

    @Override
    public CategoryEntity toEntity(Category domain) {
        CategoryEntity parentEntity = null;
        if (domain.getParent() != null && domain.getParent().getId() != null) {
            parentEntity = entityManager.getReference(CategoryEntity.class, domain.getParent().getId());
        }

        return CategoryEntity.builder()
            .id(domain.getId())
            .name(domain.getName())
            .image(domain.getImage())
            .description(domain.getDescription())
            .parent(parentEntity)
            .build();
    }
}
