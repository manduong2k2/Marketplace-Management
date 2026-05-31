package com.StoreManagement.Catalog.Infrastructure.Persistence.Repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.StoreManagement.Catalog.Domain.Contract.IBrandRepository;
import com.StoreManagement.Catalog.Domain.Models.Brand;
import com.StoreManagement.Catalog.Infrastructure.Persistence.Entity.BrandEntity;

@Repository
public class BrandRepository implements IBrandRepository {

    private final BrandJpaRepository jpaRepository;

    public BrandRepository(BrandJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Brand> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
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