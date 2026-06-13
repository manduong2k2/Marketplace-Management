package com.Marketplace_Management.Auth.Mapper;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Auth.Entity.RoleEntity;
import com.Marketplace_Management.Auth.Models.Role;
import com.Marketplace_Management.Shared.Domain.Contracts.IMapper;

@Component
public class RoleMapper implements IMapper<Role, RoleEntity>{
    @Override
    public Role toDomain(RoleEntity entity) {
        return new Role(
            entity.getId(),
            entity.getName(),
            entity.getCode()
        );
    }
    
    @Override
    public RoleEntity toEntity(Role domain) {
        return new RoleEntity(
            domain.getId(),
            domain.getName(),
            domain.getCode()
        );
    }
}
