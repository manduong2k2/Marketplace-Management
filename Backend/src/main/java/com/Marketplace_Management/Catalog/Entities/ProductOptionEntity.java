package com.Marketplace_Management.Catalog.Entities;

import org.hibernate.annotations.Nationalized;

import com.Marketplace_Management.Shared.Entities.IntegerEntity;

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

@Entity
@Table(name = "product_options")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductOptionEntity extends IntegerEntity {
    @Column(nullable = false)
    @Size(max = 100)
    @Nationalized
    private String name;

    @Column(nullable = false)
    @Size(max = 100)
    @Nationalized
    private String value;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    public ProductOptionEntity(Integer id, String name, String value, ProductEntity product) {
        if(id != null) this.setId(id);
        this.name = name;
        this.value = value;
        this.product = product;
    }
}
