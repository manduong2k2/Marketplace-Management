package com.Marketplace_Management.Order.Models;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Shared.Models.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data 
@EqualsAndHashCode(callSuper = false)
@SuperBuilder 
@AllArgsConstructor 
@NoArgsConstructor
public class OrderItem extends Entity<UUID>{
    private UUID productId;
    private int quantity;
    private double total;
    private String productName;
    private String productSku;
    private double productPrice;
    private List<String> productImages;
    private String productDescription;

    //Business methods

    public void plusOne() {
        this.quantity++;
    }

    public void minusOne() {
        this.quantity--;
    }

    public double calculateTotal() {
        return this.quantity * this.productPrice;
    }

    public double getTotal() {
        return this.total;
    }
}
