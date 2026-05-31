package com.StoreManagement.Auth.Domain.Models;

import java.util.UUID;

import com.StoreManagement.Shared.Domain.AggregateRoot;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Role extends AggregateRoot<UUID> {
    private String code;
    private String name;

    public Role(UUID id, String code, String name) {
        super(id);
        this.code = code;
        this.name = name;
    }
}
