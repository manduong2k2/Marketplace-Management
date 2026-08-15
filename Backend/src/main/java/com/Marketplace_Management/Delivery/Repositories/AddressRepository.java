package com.Marketplace_Management.Delivery.Repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.Marketplace_Management.Delivery.Contracts.IAddressRepository;
import com.Marketplace_Management.Delivery.Entities.AddressEntity;
import com.Marketplace_Management.Delivery.Entities.WardEntity;
import com.Marketplace_Management.Delivery.Models.Address;
import com.Marketplace_Management.Delivery.Models.Province;
import com.Marketplace_Management.Delivery.Models.Ward;
import com.Marketplace_Management.Delivery.Mappers.AddressMapper;

import jakarta.persistence.EntityManager;

@Repository
public class AddressRepository implements IAddressRepository {
    private final JpaAddressRepository addressRepository;
    private final JpaProvinceRepository provinceRepository;
    private final AddressMapper addressMapper;
    private final EntityManager entityManager;
    
    public AddressRepository(JpaAddressRepository addressRepository, JpaProvinceRepository provinceRepository, AddressMapper addressMapper, EntityManager entityManager) {
        this.addressRepository = addressRepository;
        this.provinceRepository = provinceRepository;
        this.addressMapper = addressMapper;
        this.entityManager = entityManager;
    }
    
    @Override
    public List<Province> getMasterRegions() {
        return provinceRepository.findAll().stream().map(p -> Province.builder()
                .id(p.getId())
                .name(p.getName())
                .fullName(p.getFullName())
                .wards(p.getWards().stream().map(w -> Ward.builder()
                        .id(w.getId())
                        .name(w.getName())
                        .fullName(w.getFullName())
                        .build()).toList())
                .build()).toList();
    }

    @Override
    public List<Address> getMyAddresses(UUID userId) {
        return addressRepository.findByUserId(userId).stream().map(addressMapper::toDomain).toList();
    }
    
    @Override
    public Address createAddress(Address address) {
        AddressEntity addressEntity = addressMapper.toEntity(address);
        addressEntity.setWard(entityManager.getReference(WardEntity.class, address.getWardId()));
        var saved = addressRepository.save(addressEntity);
        return addressMapper.toDomain(saved);
    }
    
    @Override
    public Address upsirtAddress(Address address) {
        AddressEntity entity = addressMapper.toEntity(address);
        if (address.getWardId() != null) {
            entity.setWard(entityManager.getReference(WardEntity.class, address.getWardId()));
        }
        var saved = addressRepository.save(entity);
        return addressMapper.toDomain(saved);
    }
    
    @Override
    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }
    
    @Override
    public Address getAddressById(Long id) {
        var address = addressRepository.findById(id).orElse(null);
        var domain = address != null ? addressMapper.toDomain(address) : null;
        return domain;
    }
    
    @Override
    public Address getMyDefaultAddress(UUID userId) {
        var address = addressRepository.findFirstByUserIdAndIsDefault(userId, true);
        return address != null ? addressMapper.toDomain(address) : null;
    }
}
