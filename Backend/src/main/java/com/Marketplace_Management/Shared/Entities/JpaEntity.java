package com.Marketplace_Management.Shared.Entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.hibernate.annotations.UpdateTimestamp;

import com.Marketplace_Management.Shared.Events.EntityEventListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SoftDelete(columnName = "deleted_at", strategy = SoftDeleteType.TIMESTAMP)
@Data
@EqualsAndHashCode(callSuper = false)
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@EntityListeners(EntityEventListener.class) // publishes EntityEvent<T> on create/update/delete for every entity
public abstract class JpaEntity {
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    protected LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;
}
