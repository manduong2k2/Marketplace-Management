package com.StoreManagement.Auth.Infrastructure.Persistence.Entity;

import java.util.UUID;

import com.StoreManagement.Auth.Domain.Models.Role;
import com.StoreManagement.Shared.Infrastructure.Persistence.UuidEntity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "roles")
@Data
@EqualsAndHashCode(callSuper = false)
public class RoleEntity extends UuidEntity {

    @Column(unique = false, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String code;

    public RoleEntity() {}

    public RoleEntity(UUID id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public Role toDomain() {
        return new Role(id, code, name);
    }
}