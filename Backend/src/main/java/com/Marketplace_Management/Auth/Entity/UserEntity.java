package com.Marketplace_Management.Auth.Entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.Marketplace_Management.Shared.Infrastructure.Persistence.UuidEntity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "users")
@EqualsAndHashCode(callSuper = false)
public class UserEntity extends UuidEntity {
    @Column(unique = true, columnDefinition = "varchar(255)", nullable = false)
    private String email;

    @Column(columnDefinition = "varchar(200)", nullable = false)
    private String password;

    @Column(columnDefinition = "varchar(255)", nullable = true)
    private String name;

    @Column(columnDefinition = "varchar(255)", nullable = true)
    private String avatar;

    @Column(unique = true, columnDefinition = "varchar(255)", nullable = true)
    private String phone;

    @Column(columnDefinition = "varchar(20) default 'INACTIVE'", nullable = true)
    private String status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<RoleEntity> roles;

    public UserEntity() {
        this.roles = new java.util.HashSet<>();
    }

    public UserEntity(String email, String password, String status) {
        this.email = email;
        this.password = password;
        this.status = status;
    }

    public UserEntity(UUID id, String email, String password, String status, String name, String avatar, String phone, Set<RoleEntity> roles) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.status = status;
        this.name = name;
        this.avatar = avatar;
        this.phone = phone;
        this.roles = roles;
    }
}
