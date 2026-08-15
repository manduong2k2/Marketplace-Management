package com.Marketplace_Management.Catalog.Entities;

import org.hibernate.annotations.Nationalized;

import com.Marketplace_Management.Shared.Entities.NumericEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "product_options")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class ProductOptionEntity extends NumericEntity {
    @EqualsAndHashCode.Include
    @Column(nullable = false)
    @Size(max = 100)
    @Nationalized
    private String name;

    @EqualsAndHashCode.Include
    @Column(nullable = false)
    @Size(max = 100)
    @Nationalized
    private String value;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    public ProductOptionEntity(Long id, String name, String value, ProductEntity product) {
        if(id != null) this.setId(id);
        this.name = name;
        this.value = value;
        this.product = product;
    }
}
