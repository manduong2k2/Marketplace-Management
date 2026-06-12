package com.Marketplace_Management.Catalog.Infrastructure.Persistence.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Nationalized;
import com.Marketplace_Management.Shared.Infrastructure.Persistence.UuidEntity;

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
public class CategoryEntity extends UuidEntity {
    @Column(nullable = false)
    @Size(max = 100)
    @Nationalized
    private String name;

    @Column(nullable = true)
    private String image;

    @Column(nullable = true, length = 500)
    @Nationalized
    private String description;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private CategoryEntity parent;

    @OneToMany(mappedBy = "parent",
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<CategoryEntity> children = new ArrayList<>();

    public CategoryEntity(UUID id, String name, String image, String description, CategoryEntity parent) {
        this.setId(id);
        this.name = name;
        this.image = image;
        this.description = description;
        this.parent = parent;
    }
}