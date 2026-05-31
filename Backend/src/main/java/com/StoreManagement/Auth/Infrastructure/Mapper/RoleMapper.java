package com.StoreManagement.Auth.Infrastructure.Mapper;

import org.springframework.stereotype.Component;

import com.StoreManagement.Auth.Domain.Models.Role;
import com.StoreManagement.Auth.Infrastructure.Persistence.Entity.RoleEntity;
import com.StoreManagement.Shared.Domain.Contracts.IMapper;

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
