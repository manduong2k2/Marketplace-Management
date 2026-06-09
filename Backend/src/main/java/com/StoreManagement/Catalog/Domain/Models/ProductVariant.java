package com.StoreManagement.Catalog.Domain.Models;

import java.util.List;
import java.util.UUID;

import com.StoreManagement.Shared.Domain.AggregateRoot;
import com.StoreManagement.Shared.Domain.File;


public class ProductVariant extends AggregateRoot<UUID>{
    private UUID productId;
    private String name;
    private String code;
    private Money price = new Money(0);
    private int stock = 0;
    private List<File> files;

    public ProductVariant(UUID id, UUID productId, String name, String code, double price, int stock, List<File> files) {
        super(id);
        this.productId = productId;
        this.name = name;
        this.code = code;
        this.price = new Money(price);
        this.stock = stock;
        this.files = files;
    }
    
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
}
