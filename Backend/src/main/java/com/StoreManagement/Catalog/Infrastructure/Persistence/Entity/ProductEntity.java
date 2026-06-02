package com.StoreManagement.Catalog.Infrastructure.Persistence.Entity;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SQLRestriction;
import com.StoreManagement.Shared.Infrastructure.Persistence.JpaEntity;
import com.StoreManagement.Shared.Infrastructure.Persistence.Entity.FileEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_product_brand_price", columnList = "brand_id, price"),
        @Index(name = "idx_product_status", columnList = "status")
    }
)
@EqualsAndHashCode(callSuper = false)
@Data
public class ProductEntity extends JpaEntity {
    @Column(nullable = false)
    @Size(max = 100)
    @Nationalized
    private String name;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int stock;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private BrandEntity brand;

    @ManyToMany
    @JoinTable(name = "product_category", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<CategoryEntity> categories;

    @Column(nullable = false)
    private String status;

    @Column(nullable = true)
    @Size(max = 500)
    @Nationalized
    private String description;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "entity_id", referencedColumnName = "id", insertable = false, updatable = false)
    @SQLRestriction("entity_type = 'Product'")
    private List<FileEntity> files;

    public ProductEntity() {
    }

    public ProductEntity(UUID id, String name, String description) {
        this.setId(id);
        this.name = name;
        this.description = description;
    }
}
