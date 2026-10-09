package com.Marketplace_Management.Auth.Mappers;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Auth.Entities.UserEntity;
import com.Marketplace_Management.Auth.Models.User;
import com.Marketplace_Management.Shared.Contracts.EntityDomainMapper;

@Component
public class UserMapper implements EntityDomainMapper<User, UserEntity>{
    private final RoleMapper roleMapper;

    public UserMapper(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    public User toDomain(UserEntity entity) {
        return User.builder()
            .id(entity.getId())
            .email(entity.getEmail())
            .password(entity.getPassword())
            .name(entity.getName())
            .avatar(entity.getAvatar())
            .phone(entity.getPhone())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .roles(entity.getRoles().stream().map(roleMapper::toDomain).collect(java.util.stream.Collectors.toSet()))
            .build();
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
