package com.Marketplace_Management.Vendor.Repositories;

import com.Marketplace_Management.Vendor.Contracts.IVendorRepository;
import com.Marketplace_Management.Vendor.Entities.VendorEntity;
import com.Marketplace_Management.Vendor.Models.Vendor;
import com.Marketplace_Management.Shared.Contracts.IMapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class VendorRepository implements IVendorRepository {

    private final VendorJpaRepository jpaRepository;

    private IMapper<Vendor, VendorEntity> vendorMapper;

    public VendorRepository(VendorJpaRepository jpaRepository, IMapper<Vendor, VendorEntity> vendorMapper) {
        this.jpaRepository = jpaRepository;
        this.vendorMapper = vendorMapper;
    }

    @Override
    public List<Vendor> findAll() {
        return jpaRepository.findAll().stream()
                .map(vendorMapper::toDomain)
                .toList();
    }

    @Override
    public Vendor save(Vendor vendor) {
        VendorEntity entity = vendorMapper.toEntity(vendor);
        VendorEntity saved = jpaRepository.save(entity);
        return vendorMapper.toDomain(saved);
    }

    @Override
    public Optional<Vendor> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(vendorMapper::toDomain);
    }

    @Override
    public Optional<Vendor> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId)
                .map(vendorMapper::toDomain);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).isEmpty()
            ? false : true;
    }
}