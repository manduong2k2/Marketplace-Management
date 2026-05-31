package com.StoreManagement.Catalog.Infrastructure.Persistence.Repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.StoreManagement.Catalog.Domain.Contract.ICategoryRepository;
import com.StoreManagement.Catalog.Domain.Models.Category;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.CategoryEntity;

@Repository
public class CategoryRepository implements ICategoryRepository {

    private final CategoryJpaRepository jpaRepository;

    public CategoryRepository(CategoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Category save(Category Category) {
        CategoryEntity entity = toEntity(Category);
        CategoryEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(this::toDomain);
    }
    
    @Override
    public Category update(Category Category) {
        CategoryEntity entity = toEntity(Category);
        CategoryEntity updated = jpaRepository.save(entity);
        return toDomain(updated);
    }
    
    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    // ===== mapping =====

    private Category toDomain(CategoryEntity entity) {
        return new Category(
                entity.getId(),
                entity.getName(),
                entity.getParentId(),
                entity.getImage(),
                entity.getDescription()
        );
    }

    private CategoryEntity toEntity(Category Category) {
        return new CategoryEntity(
                Category.getId(),
                Category.getName(),
                Category.getParentId(),
                Category.getImage(),
                Category.getDescription()
        );
    }
}