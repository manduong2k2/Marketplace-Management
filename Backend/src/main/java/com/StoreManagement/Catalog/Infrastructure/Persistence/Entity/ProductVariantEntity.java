package com.StoreManagement.Catalog.Infrastructure.Persistence.Entity;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Nationalized;

import com.StoreManagement.Shared.Infrastructure.Persistence.JpaEntity;
import com.StoreManagement.Shared.Infrastructure.Persistence.Entity.FileEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "product_variants")
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public class ProductVariantEntity extends JpaEntity{
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

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "entity_id", referencedColumnName = "id", insertable = false, updatable = false)
    @SQLRestriction("entity_type = 'ProductVariant'")
    private List<FileEntity> files;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    public ProductVariantEntity(UUID id, String name, String code, double price, int stock, List<FileEntity> files, ProductEntity product) {
        this.setId(id);
        this.setName(name);
        this.setCode(code);
        this.setPrice(price);
        this.setStock(stock);
        this.setFiles(files);
    }
}
