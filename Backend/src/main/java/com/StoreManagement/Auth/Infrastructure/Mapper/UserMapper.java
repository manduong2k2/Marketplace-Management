package com.StoreManagement.Auth.Infrastructure.Mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.StoreManagement.Auth.Domain.Models.User;
import com.StoreManagement.Auth.Infrastructure.Persistence.Entity.UserEntity;
import com.StoreManagement.Shared.Domain.Contracts.IMapper;

@Component
public class UserMapper implements IMapper<User, UserEntity>{
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public User toDomain(UserEntity entity) {
        return new User(
            entity.getId(),
            entity.getEmail(),
            entity.getPassword(),
            entity.getName(),
            entity.getAvatar(),
            entity.getPhone(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getRoles().stream().map(roleMapper::toDomain).collect(java.util.stream.Collectors.toSet())
        );
    }
    
    @Override
    public UserEntity toEntity(User domain) {
        return new UserEntity(
            domain.getId(),
            domain.getEmail(),
            domain.getPassword(),
            domain.getStatus(),
            domain.getName(),
            domain.getAvatar(),
            domain.getPhone(),
            domain.getRoles().stream().map(roleMapper::toEntity).collect(java.util.stream.Collectors.toSet())
        );
    }
}
