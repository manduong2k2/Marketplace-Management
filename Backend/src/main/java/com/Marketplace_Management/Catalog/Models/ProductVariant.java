package com.Marketplace_Management.Catalog.Models;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Shared.Models.Entity;
import com.Marketplace_Management.Shared.Models.File;


public class ProductVariant extends Entity<UUID> {
    
    private UUID productId;
    private String name;
    private String code;
    private Money price = new Money(0);
    private int stock = 0;
    private List<File> files;
    private Product product;
    private List<ProductOption> options;
    private String optionList;

    public ProductVariant() {
        super(null);
    }

    private ProductVariant(Builder builder) {
        super(builder.id);
        this.productId  = builder.productId;
        this.name       = builder.name;
        this.code       = builder.code;
        this.price      = builder.price;
        this.stock      = builder.stock;
        this.files      = builder.files;
        this.product    = builder.product;
        this.options    = builder.options;
        this.optionList = builder.optionList;
    }

    // Builders

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private UUID id;
        private UUID productId;
        private String name;
        private String code;
        private Money price = new Money(0);
        private int stock = 0;
        private List<File> files;
        private Product product;
        private List<ProductOption> options;
        private String optionList;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder productId(UUID productId) {
            this.productId = productId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder price(double price) {
            this.price = new Money(price);
            return this;
        }

        public Builder stock(int stock) {
            this.stock = stock;
            return this;
        }

        public Builder files(List<File> files) {
            this.files = files;
            return this;
        }

        public Builder product(Product product) {
            this.product = product;
            return this;
        }

        public Builder options(List<ProductOption> options) {
            this.options = options;
            return this;
        }

        public Builder optionList(String optionList) {
            this.optionList = optionList;
            return this;
        }

        public ProductVariant build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Variant name is required");
            }

            return new ProductVariant(this);
        }
    }

    //Getters / Setters
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getProductId() {
        return productId;
    }
    
    public void setProductId(UUID productId) {
        this.productId = productId;
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
    
    public void setPrice(Money price) {
        this.price = price;
    }
    
    public int getStock() {
        return stock;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
    }
    
    public List<File> getFiles() {
        return files;
    }
    
    public void setFiles(List<File> files) {
        this.files = files;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }

    public List<ProductOption> getOptions() {
        return options;
    }
    
    public void setOptions(List<ProductOption> options) {
        this.options = options;
    }

    public void setOptionList(String optionList) {
        this.optionList = optionList;
    }

    public String getOptionList() {
        return optionList;
    }
}
