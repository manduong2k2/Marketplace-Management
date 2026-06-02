package com.StoreManagement.Catalog.Infrastructure.Mapper;

import org.springframework.stereotype.Component;

import com.StoreManagement.Catalog.Domain.Models.Category;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.CategoryEntity;
import com.StoreManagement.Shared.Domain.Contracts.IMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class CategoryMapper implements IMapper<Category, CategoryEntity>{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Category toDomain(CategoryEntity entity) {
        Category parent = null;
        if (entity.getParent() != null) {
            parent = new Category(
                entity.getParent().getId(),
                entity.getParent().getName(),
                entity.getParent().getImage(),
                entity.getParent().getDescription(),
                null,
                null);
        }
        
        Category category = new Category(
            entity.getId(), 
            entity.getName(), 
            entity.getImage(), 
            entity.getDescription(), 
            parent, 
            entity.getChildren().isEmpty() ? java.util.List.of() : entity.getChildren().stream().map(this::toDomain).toList());
        return category;
    }

    @Override
    public CategoryEntity toEntity(Category domain) {
        CategoryEntity parentEntity = null;
        if (domain.getParent() != null && domain.getParent().getId() != null) {
            parentEntity = entityManager.getReference(CategoryEntity.class, domain.getParent().getId());
        }
        
        CategoryEntity entity = new CategoryEntity(
            domain.getId(), 
            domain.getName(), 
            domain.getImage(), 
            domain.getDescription(),
            parentEntity);
        return entity;
    }
    
}
