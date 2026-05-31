package com.StoreManagement.Catalog.Domain.Models;

import com.StoreManagement.Catalog.Domain.Constants.ProductStatusEnum;
import com.StoreManagement.Shared.Domain.ValueObject;

public class ProductStatus extends ValueObject{
    private String value;
    
    public ProductStatus() {}
    
    public ProductStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(ProductStatusEnum status) {
        this.value = status.name();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProductStatus that = (ProductStatus) obj;
        return value.equals(that.value);
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
