package com.Marketplace_Management.Catalog.Entities;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Nationalized;

import com.Marketplace_Management.Shared.Entities.UuidEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "brands")
@Data
@EqualsAndHashCode(callSuper = false)
public class BrandEntity extends UuidEntity {
    @Column(nullable = false)
    @Size(max = 100)
    @Nationalized
    private String name;

    @Column(nullable = true)
    private String image;

    @Column(nullable = true, length = 500)
    @Nationalized
    private String description;

    @OneToMany(mappedBy = "brand")
    private List<ProductEntity> products;

    public BrandEntity() {}

    public BrandEntity(UUID id, String name, String image, String description) {
        this.setId(id);
        this.name = name;
        this.image = image;
        this.description = description;
    }
}