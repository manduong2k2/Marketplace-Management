package com.StoreManagement.Catalog.Infrastructure.Persistence.Entity;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.StoreManagement.Shared.Infrastructure.Persistence.JpaEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "products")
@EqualsAndHashCode(callSuper = false)
@Data
public class ProductEntity extends JpaEntity {
    @Column(nullable = false)
    @Size(max = 100)
    @Nationalized
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int stock;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private BrandEntity brand;

    @ManyToMany
    @JoinTable(
        name = "product_category",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<CategoryEntity> categories;

    @Column(nullable = false)
    private String status;

    @Column(nullable = true)
    @Size(max = 500)
    @Nationalized
    private String description;
    
    public ProductEntity() {}
    
    public ProductEntity(UUID id, String name, String description) {
        this.setId(id);
        this.name = name;
        this.description = description;
    }
}
