package com.Marketplace_Management.Catalog.Entities;

import org.hibernate.annotations.Nationalized;

import com.Marketplace_Management.Shared.Entities.NumericEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "product_options")
@SuperBuilder
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

}
