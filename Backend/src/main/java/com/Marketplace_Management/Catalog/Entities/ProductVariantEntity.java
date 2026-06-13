package com.Marketplace_Management.Catalog.Entities;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.SQLRestriction;

import com.Marketplace_Management.Shared.Entities.FileEntity;
import com.Marketplace_Management.Shared.Entities.UuidEntity;

@Entity
@Table(name = "product_variants")
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public class ProductVariantEntity extends UuidEntity{
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "entity_id", referencedColumnName = "id", insertable = false, updatable = false)
    @SQLRestriction("entity_type = 'ProductVariant'")
    private List<FileEntity> files;

    @Column(nullable = true)
    private String optionList;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "product_variant_option",
        joinColumns = @JoinColumn(name = "variant_id"),
        inverseJoinColumns = @JoinColumn(name = "option_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"variant_id", "option_id"})
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ProductOptionEntity> options;

    public ProductVariantEntity(UUID id, String name, String code, double price, int stock, List<FileEntity> files, String optionList, List<ProductOptionEntity> options) {
        this.setId(id);
        this.setName(name);
        this.setCode(code);
        this.setPrice(price);
        this.setStock(stock);
        this.setFiles(files);
        this.setOptions(options);
        this.setOptionList(optionList);
    }
}
