package com.StoreManagement.Catalog.Domain.Models;

import java.util.UUID;

import com.StoreManagement.Catalog.Domain.Constants.ProductStatusEnum;
import com.StoreManagement.Shared.Domain.AggregateRoot;

import java.util.ArrayList;
import java.util.List;

public class Product extends AggregateRoot<UUID> {
    private String name;
    private String description;
    private UUID brandId;
    private Brand brand;
    private List<UUID> categoryIds;
    private List<Category> categories;
    private ProductStatus status;
    private List<ProductVariant> variants;

    public Product() {
        super(null);
        this.status = new ProductStatus();
        this.categoryIds = new ArrayList<>();
    }

    public Product(UUID id, String name, String description, UUID brandId, ProductStatus status, List<UUID> categoryIds, List<ProductVariant> variants) {
        super(id);
        this.name = name;
        this.description = description;
        this.brandId = brandId;
        this.status = status;
        this.categoryIds = categoryIds;
        this.variants = variants;
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

    public boolean isArchived() {
        return status.getValue().equals("ARCHIVED");
    }
    
    public UUID getBrandId() {
        return brandId;
    }
    
    public void setBrandId(UUID brandId) {
        this.brandId = brandId;
    }
    
    public Brand getBrand() {
        return brand;
    }
    
    public void setBrand(Brand brand) {
        this.brand = brand;
    }
    
    public List<Category> getCategories() {
        return categories;
    }
    
    public void setCategories(List<Category> categories) {
        this.categories = categories;
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

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }
}
