package com.Marketplace_Management.Auth.Domain.Contract;

import java.util.List;
import java.util.Optional;

import com.Marketplace_Management.Auth.Domain.Models.Role;

public interface IRoleRepository{
    Optional<Role> findByCode(String code);
    long count();
    List<Role> saveAll(List<Role> roles);
}
