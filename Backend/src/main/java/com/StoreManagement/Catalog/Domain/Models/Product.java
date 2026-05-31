package com.StoreManagement.Catalog.Domain.Models;

import java.util.UUID;

import com.StoreManagement.Catalog.Domain.Constants.ProductStatusEnum;
import com.StoreManagement.Shared.Domain.AggregateRoot;

import java.util.ArrayList;
import java.util.List;

public class Product extends AggregateRoot<UUID> {
    private String name;
    private String description;
    private String code;
    private Money price = new Money(0);
    private int stock = 0;
    private UUID brandId;
    private List<UUID> categoryIds;
    private ProductStatus status;

    public Product() {
        super(null);
        this.status = new ProductStatus();
        this.categoryIds = new ArrayList<>();
    }

    public Product(UUID id, String name, String description, String code, double price, int stock, UUID brandId, ProductStatus status, List<UUID> categoryIds) {
        super(id);
        this.name = name;
        this.description = description;
        this.code = code;
        this.price = new Money(price);
        this.stock = stock;
        this.brandId = brandId;
        this.status = status;
        this.categoryIds = categoryIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }

    public Money getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = new Money(price);
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        this.stock = stock;
    }
    
    public UUID getBrandId() {
        return brandId;
    }
    
    public void setBrandId(UUID brandId) {
        this.brandId = brandId;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<UUID> getCategoryIds() {
        return categoryIds;
    }
    
    public void setCategoryIds(List<UUID> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public String getStatus() {
        return status.getValue();
    }
    
    public void setStatus(ProductStatusEnum status) {
        this.status.setValue(status);
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public void setStatus(String status) {
        this.status.setValue(ProductStatusEnum.valueOf(status));
    }
}
