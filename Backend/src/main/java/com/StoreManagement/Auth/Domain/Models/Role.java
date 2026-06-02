package com.StoreManagement.Auth.Domain.Models;

import java.util.UUID;

import com.StoreManagement.Shared.Domain.AggregateRoot;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Role extends AggregateRoot<UUID> {
    private String name;
    private String code;
    

    public Role(UUID id, String name, String code) {
        super(id);
        this.name = name;
        this.code = code;
    }
}
