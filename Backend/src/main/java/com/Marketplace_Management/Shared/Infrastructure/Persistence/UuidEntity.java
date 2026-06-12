package com.Marketplace_Management.Shared.Infrastructure.Persistence;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class UuidEntity extends JpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;
}
