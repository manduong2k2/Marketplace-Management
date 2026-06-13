package com.Marketplace_Management.Auth.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.Marketplace_Management.Auth.Contracts.IRoleRepository;
import com.Marketplace_Management.Auth.Entities.RoleEntity;
import com.Marketplace_Management.Auth.Models.Role;
import com.Marketplace_Management.Shared.Contracts.IMapper;

@Repository
public class RoleRepository implements IRoleRepository {
    private final RoleJpaRepository roleJpaRepository;
    private final IMapper<Role, RoleEntity> roleMapper;

    public RoleRepository(RoleJpaRepository roleJpaRepository, IMapper<Role, RoleEntity> roleMapper) {
        this.roleJpaRepository = roleJpaRepository;
        this.roleMapper = roleMapper;
    }

    public Optional<Role> findByCode(String code) {
        return roleJpaRepository.findByCode(code).map(roleMapper::toDomain);
    }
    
    public long count() {
        return roleJpaRepository.count();
    }

    public List<Role> saveAll(List<Role> roles) {
        return roleJpaRepository.saveAll(roles.stream().map(roleMapper::toEntity).toList()).stream().map(roleMapper::toDomain).toList();
    }
}
