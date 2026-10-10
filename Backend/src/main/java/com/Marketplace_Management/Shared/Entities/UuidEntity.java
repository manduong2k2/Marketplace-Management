package com.Marketplace_Management.Shared.Entities;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class UuidEntity extends JpaEntity {
    @Id
    // UUID v7: time-ordered, so new rows are appended to the primary-key index
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    protected UUID id;
}
