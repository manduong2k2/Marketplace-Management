package com.Marketplace_Management.Catalog.Contracts;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Catalog.DTO.Commands.Product.CreateProductCommand;
import com.Marketplace_Management.Catalog.DTO.Commands.Product.GetListProductCommand;
import com.Marketplace_Management.Catalog.DTO.Commands.Product.UpdateProductCommand;
import com.Marketplace_Management.Catalog.DTO.Response.ProductResponse;
import com.Marketplace_Management.Catalog.DTO.Response.ProductVariantResponse;
import com.Marketplace_Management.Catalog.DTO.Response.Short.ProductShortResponse;
import com.Marketplace_Management.Shared.Application.DTO.Responses.PaginatedResponse;

import java.io.IOException;

public interface IProductService {
    public PaginatedResponse<ProductShortResponse> getAllProducts(GetListProductCommand command);

    public ProductResponse createProduct(CreateProductCommand command) throws IOException;

    public ProductResponse getProduct(UUID id);

    public ProductResponse updateProduct(UUID id, UpdateProductCommand command) throws IOException;

    public void deleteProduct(UUID id);
    
    public List<String> getAllStatus();

    public ProductVariantResponse getProductVariant(UUID productId, UUID productVariantId);
}
