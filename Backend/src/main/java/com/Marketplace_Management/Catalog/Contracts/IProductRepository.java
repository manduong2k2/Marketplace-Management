package com.Marketplace_Management.Catalog.Contracts;

import java.util.UUID;

import com.Marketplace_Management.Catalog.DTOs.Commands.Product.GetListProductCommand;
import com.Marketplace_Management.Catalog.DTOs.Response.Short.ProductShortResponse;
import com.Marketplace_Management.Catalog.Models.Product;
import com.Marketplace_Management.Catalog.Models.ProductVariant;
import com.Marketplace_Management.Shared.DTOs.Responses.PaginatedResponse;


public interface IProductRepository {
    Product save(Product Product);

    PaginatedResponse<ProductShortResponse> findAll(GetListProductCommand command);

    Product findById(UUID id);

    Product update(Product Product);

    void delete(UUID id);

    public ProductVariant findVariantById(UUID productId, UUID variantId);
}
