package com.Marketplace_Management.Catalog.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "product_collection")
public class ProductCollectionEntity {
    @EmbeddedId
    private ProductCollectionId id;
    
    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private ProductEntity product;
    
    @Column(nullable = false)
    private Integer displayOrder;
}
