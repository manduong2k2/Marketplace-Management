package com.StoreManagement.Catalog.Infrastructure.Persistence.Repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.StoreManagement.Catalog.Domain.Contract.ICategoryRepository;
import com.StoreManagement.Catalog.Domain.Models.Category;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.CategoryEntity;
import com.StoreManagement.Shared.Domain.Contracts.IMapper;

@Repository
public class CategoryRepository implements ICategoryRepository {

    private final CategoryJpaRepository jpaRepository;
    private final IMapper<Category, CategoryEntity> mapper;

    public CategoryRepository(CategoryJpaRepository jpaRepository, IMapper<Category, CategoryEntity> mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
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