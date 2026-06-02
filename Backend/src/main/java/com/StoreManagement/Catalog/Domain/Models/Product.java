package com.StoreManagement.Catalog.Domain.Models;

import java.util.UUID;

import com.StoreManagement.Catalog.Domain.Constants.ProductStatusEnum;
import com.StoreManagement.Shared.Domain.AggregateRoot;
import com.StoreManagement.Shared.Domain.File;

import java.util.ArrayList;
import java.util.List;

public class Product extends AggregateRoot<UUID> {
    private String name;
    private String description;
    private String code;
    private Money price = new Money(0);
    private int stock = 0;
    private UUID brandId;
    private Brand brand;
    private List<UUID> categoryIds;
    private List<Category> categories;
    private ProductStatus status;
    private List<File> files;

    public Product() {
        super(null);
        this.status = new ProductStatus();
        this.categoryIds = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    public Product(UUID id, String name, String description, String code, double price, int stock, UUID brandId, ProductStatus status, List<UUID> categoryIds, List<File> files) {
        super(id);
        this.name = name;
        this.description = description;
        this.code = code;
        this.price = new Money(price);
        this.stock = stock;
        this.brandId = brandId;
        this.status = status;
        this.categoryIds = categoryIds;
        this.files = files != null ? files : new ArrayList<>();
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

    public boolean isOutOfStock() {
        return stock == 0;
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

    public List<File> getFiles() {
        return files;
    }
    
    public void setFiles(List<File> files) {
        this.files = files;
    }
}
