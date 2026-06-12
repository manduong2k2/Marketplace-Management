package com.Marketplace_Management.Catalog.Infrastructure.Persistence.Entity;

import java.util.List;

import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.Marketplace_Management.Shared.Infrastructure.Persistence.UuidEntity;

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
public class ProductEntity extends UuidEntity {
    @Column(nullable = false)
    @Size(max = 100)
    @Nationalized
    private String name;

    @Column(nullable = false)
    private String status;

    @Column(nullable = true)
    @Size(max = 500)
    @Nationalized
    private String description;

    // relationships

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private BrandEntity brand;

    @ManyToMany
    @JoinTable(name = "product_category", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<CategoryEntity> categories;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ProductVariantEntity> variants;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ProductOptionEntity> options;

    //

    public ProductEntity() {
    }
}
