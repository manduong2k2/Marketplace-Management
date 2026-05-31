package com.StoreManagement.Catalog.Infrastructure.Persistence.Entity;

import java.util.UUID;

import org.hibernate.annotations.Nationalized;
import com.StoreManagement.Shared.Infrastructure.Persistence.JpaEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CategoryEntity extends JpaEntity {
    @Column(nullable = false)
    @Size(max = 100)
    @Nationalized
    private String name;
    
    @Column(nullable = true)
    private UUID parentId;

    @Column(nullable = true)
    private String image;

    @Column(nullable = true, length = 500)
    @Nationalized
    private String description;

    public CategoryEntity(UUID id, String name, UUID parentId, String image, String description) {
        this.setId(id);
        this.name = name;
        this.parentId = parentId;
        this.image = image;
        this.description = description;
    }
}