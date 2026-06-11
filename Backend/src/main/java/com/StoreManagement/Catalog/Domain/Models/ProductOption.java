package com.StoreManagement.Catalog.Domain.Models;

import java.util.UUID;

import com.StoreManagement.Shared.Domain.Entity;

public class ProductOption extends Entity<Integer> {

    private String name;
    private String value;
    private UUID productId;
    private Product product;

    public ProductOption() {
        super(null);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        
        private Integer id;
        private String name;
        private String value;
        private UUID productId;
        private Product product;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder productId(UUID productId) {
            this.productId = productId;
            return this;
        }

        public Builder product(Product product) {
            this.product = product;
            return this;
        }

        public ProductOption build() {
            ProductOption productOption = new ProductOption();
            productOption.setId(this.id);
            productOption.setName(this.name);
            productOption.setValue(this.value);
            productOption.setProductId(this.productId);
            productOption.setProduct(this.product);
            return productOption;
        }
    }

    //Getters / Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
