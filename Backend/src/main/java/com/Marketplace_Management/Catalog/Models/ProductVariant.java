package com.Marketplace_Management.Catalog.Models;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.Marketplace_Management.Shared.Models.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class ProductVariant extends Entity<UUID> {
    private UUID productId;
    private String name;
    private String sku;
    private Money price;
    private int stock;
    private List<File> images;
    private Product product;
    private Set<ProductOption> options;
    private String optionList;

    /** An unset price reads as 0. */
    public Money getPrice() {
        return price != null ? price : new Money(0);
    }

    /** The builder takes the price as a plain amount. */
    public abstract static class ProductVariantBuilder<C extends ProductVariant, B extends ProductVariantBuilder<C, B>>
            extends Entity.EntityBuilder<UUID, C, B> {
        public B price(double price) {
            this.price = new Money(price);
            return self();
        }
    }
}
