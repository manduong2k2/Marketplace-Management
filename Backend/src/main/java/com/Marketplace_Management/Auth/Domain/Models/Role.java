package com.Marketplace_Management.Auth.Domain.Models;

import java.util.UUID;

import com.Marketplace_Management.Shared.Domain.AggregateRoot;

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
