package com.StoreManagement.Auth.Infrastructure.Persistence.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.StoreManagement.Auth.Domain.Contract.IRoleRepository;
import com.StoreManagement.Auth.Domain.Models.Role;
import com.StoreManagement.Auth.Infrastructure.Persistence.Entity.RoleEntity;
import com.StoreManagement.Shared.Domain.Contracts.IMapper;

@Repository
public class RoleRepository implements IRoleRepository {
    @Autowired
    private RoleJpaRepository roleJpaRepository;

    @Autowired
    private IMapper<Role, RoleEntity> roleMapper;

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
