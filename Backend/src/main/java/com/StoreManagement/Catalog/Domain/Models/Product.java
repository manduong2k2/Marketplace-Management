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

    // Builder methods

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String name;
        private String description;
        private UUID brandId;
        private Brand brand;
        private List<UUID> categoryIds;
        private List<Category> categories;
        private ProductStatus status;
        private List<ProductVariant> variants;
        
        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder brandId(UUID brandId) {
            this.brandId = brandId;
            return this;
        }
        
        public Builder brand(Brand brand) {
            this.brand = brand;
            return this;
        }
        
        public Builder categoryIds(List<UUID> categoryIds) {
            this.categoryIds = categoryIds;
            return this;
        }
        
        public Builder categories(List<Category> categories) {
            this.categories = categories;
            return this;
        }
        
        public Builder status(String status) {
            this.status = new ProductStatus(status);
            return this;
        }
        
        public Builder variants(List<ProductVariant> variants) {
            this.variants = variants;
            return this;
        }
        
        public Product build() {
            Product product = new Product();
            product.setId(this.id);
            product.setName(this.name);
            product.setDescription(this.description);
            product.setBrandId(this.brandId);
            product.setBrand(this.brand);
            product.setCategoryIds(this.categoryIds);
            product.setCategories(this.categories);
            product.setStatus(this.status);
            product.setVariants(this.variants);
            return product;
        }
    }

    // Getters / Setters

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
