package com.StoreManagement.Order.Infrastructure.Persistence.Entities;

import java.util.List;
import java.util.UUID;

import com.StoreManagement.Shared.Infrastructure.Persistence.JpaEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_snapshots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductSnapShotEntity extends JpaEntity {
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItemEntity orderItem;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_price", nullable = false)
    private double productPrice;

    @Column(name = "product_images")
    private List<String> productImages;

    @Column(name = "product_description")
    private String productDescription;

    public ProductSnapShotEntity(UUID id, String productCode, String productName, UUID productId, double productPrice, List<String> productImages, String productDescription) {
        this.setId(id);
        this.productCode = productCode;
        this.productName = productName;
        this.productId = productId;
        this.productPrice = productPrice;
        this.productImages = productImages;
        this.productDescription = productDescription;
    }
}
