package com.Marketplace_Management.Delivery.Mappers;

import com.Marketplace_Management.Delivery.Entities.AddressEntity;
import com.Marketplace_Management.Delivery.Models.Address;
import com.Marketplace_Management.Shared.Contracts.EntityDomainMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper extends EntityDomainMapper<Address, AddressEntity> {
    @Override
    @Mapping(source = "ward.id", target = "wardId")
    @Mapping(target = "ward.province.wards", ignore = true)
    Address toDomain(AddressEntity entity);
}
