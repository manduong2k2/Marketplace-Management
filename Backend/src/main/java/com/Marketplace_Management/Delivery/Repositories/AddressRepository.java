package com.Marketplace_Management.Delivery.Repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.Marketplace_Management.Delivery.Contracts.IAddressRepository;
import com.Marketplace_Management.Delivery.Entities.AddressEntity;
import com.Marketplace_Management.Delivery.Entities.WardEntity;
import com.Marketplace_Management.Delivery.Mappers.AddressMapper;
import com.Marketplace_Management.Delivery.Models.Address;
import com.Marketplace_Management.Delivery.Models.Province;
import com.Marketplace_Management.Delivery.Models.Ward;

import jakarta.persistence.EntityManager;

@Repository
public class AddressRepository implements IAddressRepository {
    private final JpaAddressRepository addressRepository;
    private final JpaProvinceRepository provinceRepository;
    private final JpaWardRepository wardRepository;
    private final AddressMapper addressMapper;
    private final EntityManager entityManager;

    public AddressRepository(JpaAddressRepository addressRepository, JpaProvinceRepository provinceRepository,
            JpaWardRepository wardRepository, AddressMapper addressMapper, EntityManager entityManager) {
        this.addressRepository = addressRepository;
        this.provinceRepository = provinceRepository;
        this.wardRepository = wardRepository;
        this.addressMapper = addressMapper;
        this.entityManager = entityManager;
    }

    @Override
    public List<Province> findProvinces() {
        return provinceRepository.findAll().stream()
                .map(p -> Province.builder().id(p.getId()).name(p.getName()).fullName(p.getFullName()).build())
                .toList();
    }

    @Override
    public List<Ward> findWardsByProvince(String provinceId) {
        return wardRepository.findByProvinceId(provinceId).stream()
                .map(w -> Ward.builder().id(w.getId()).name(w.getName()).fullName(w.getFullName()).build())
                .toList();
    }

    @Override
    public boolean wardExists(String wardId) {
        return wardRepository.existsById(wardId);
    }

    @Override
    public List<Address> findByUser(UUID userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId).stream()
                .map(addressMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Address> findByIdAndUser(Long addressId, UUID userId) {
        return addressRepository.findByIdAndUserId(addressId, userId).map(addressMapper::toDomain);
    }

    @Override
    public Optional<Address> findDefault(UUID userId) {
        return addressRepository.findFirstByUserIdAndIsDefaultTrue(userId).map(addressMapper::toDomain);
    }

    @Override
    public Optional<Address> findLatest(UUID userId) {
        return addressRepository.findFirstByUserIdOrderByCreatedAtDesc(userId).map(addressMapper::toDomain);
    }

    @Override
    public Address save(Address address) {
        AddressEntity entity = addressMapper.toEntity(address);
        entity.setWard(entityManager.getReference(WardEntity.class, address.getWardId()));
        AddressEntity saved = addressRepository.saveAndFlush(entity);
        // Reload so the response has the ward/province names (the reference above is an empty proxy)
        entityManager.refresh(saved);
        return addressMapper.toDomain(saved);
    }

    @Override
    public void delete(Long addressId) {
        addressRepository.deleteById(addressId);
    }

    @Override
    public void clearDefault(UUID userId) {
        addressRepository.clearDefault(userId);
    }
}
