package com.StoreManagement.Catalog.Domain.Contract;

import java.util.UUID;

import com.StoreManagement.Catalog.Application.DTO.Commands.Product.GetListProductCommand;
import com.StoreManagement.Catalog.Domain.Models.Product;
import com.StoreManagement.Catalog.Domain.Models.ProductVariant;
import com.StoreManagement.Shared.Application.DTO.Responses.PaginatedResponse;


public interface IProductRepository {
    Product save(Product Product);

    PaginatedResponse<Product> findAll(GetListProductCommand command);

    Product findById(UUID id);

    Product update(Product Product);

    void delete(UUID id);

    public ProductVariant findVariantById(UUID productId, UUID variantId);
}
