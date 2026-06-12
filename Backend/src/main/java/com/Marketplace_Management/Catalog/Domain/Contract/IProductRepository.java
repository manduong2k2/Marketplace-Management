package com.Marketplace_Management.Catalog.Domain.Contract;

import java.util.UUID;

import com.Marketplace_Management.Catalog.Application.DTO.Commands.Product.GetListProductCommand;
import com.Marketplace_Management.Catalog.Domain.Models.Product;
import com.Marketplace_Management.Catalog.Domain.Models.ProductVariant;
import com.Marketplace_Management.Shared.Application.DTO.Responses.PaginatedResponse;


public interface IProductRepository {
    Product save(Product Product);

    PaginatedResponse<Product> findAll(GetListProductCommand command);

    Product findById(UUID id);

    Product update(Product Product);

    void delete(UUID id);

    public ProductVariant findVariantById(UUID productId, UUID variantId);
}
