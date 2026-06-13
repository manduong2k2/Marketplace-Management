package com.Marketplace_Management.Order.Models;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Shared.Domain.Entity;

public class ProductSnapShot extends Entity<UUID>{
    private UUID productId;
    private String productName;
    private String productCode;
    private double productPrice;
    private List<String> productImages;
    private String productDescription;

    public ProductSnapShot() {
        super(null);
    }

    public ProductSnapShot(UUID id, UUID productId, String productName, String productCode, 
            double productPrice, List<String> productImages, String productDescription) {
        super(id);
        this.productId = productId;
        this.productName = productName;
        this.productCode = productCode;
        this.productPrice = productPrice;
        this.productImages = productImages;
        this.productDescription = productDescription;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public double getProductPrice() {
        return productPrice;
    }
    
    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }
    
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public List<String> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<String> productImages) {
        this.productImages = productImages;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
}
