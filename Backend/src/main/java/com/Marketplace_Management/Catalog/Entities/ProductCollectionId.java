package com.Marketplace_Management.Catalog.Entities;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class ProductCollectionId {
    private Long productId;
    private Long collectionId;
}
