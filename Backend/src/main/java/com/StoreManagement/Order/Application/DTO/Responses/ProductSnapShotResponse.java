package com.StoreManagement.Order.Application.DTO.Responses;

import java.util.List;
import java.util.UUID;

import com.StoreManagement.Order.Domain.Models.ProductSnapShot;

import lombok.Data;

@Data
public class ProductSnapShotResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private String productCode;
    private double price;
    private List<String> productImages;
    private String productDescription;

    public static ProductSnapShotResponse from(ProductSnapShot snap) {
        if (snap == null) return null;
        ProductSnapShotResponse r = new ProductSnapShotResponse();
        r.id                    = snap.getId();
        r.productId             = snap.getProductId();
        r.productName           = snap.getProductName();
        r.productCode           = snap.getProductCode();
        r.price                 = snap.getProductPrice();
        r.productImages         = snap.getProductImages();
        r.productDescription    = snap.getProductDescription();
        return r;
    }
}
