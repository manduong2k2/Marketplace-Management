package com.Marketplace_Management.Catalog.Domain.Contract;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Catalog.Application.DTO.Commands.Product.CreateProductCommand;
import com.Marketplace_Management.Catalog.Application.DTO.Commands.Product.GetListProductCommand;
import com.Marketplace_Management.Catalog.Application.DTO.Commands.Product.UpdateProductCommand;
import com.Marketplace_Management.Catalog.Application.DTO.Response.ProductResponse;
import com.Marketplace_Management.Catalog.Application.DTO.Response.ProductVariantResponse;
import com.Marketplace_Management.Shared.Application.DTO.Responses.PaginatedResponse;

import java.io.IOException;

public interface IProductService {
    public PaginatedResponse<ProductResponse> getAllProducts(GetListProductCommand command);

    public ProductResponse createProduct(CreateProductCommand command) throws IOException;

    public ProductResponse getProduct(UUID id);

    public ProductResponse updateProduct(UUID id, UpdateProductCommand command) throws IOException;

    public void deleteProduct(UUID id);
    
    public List<String> getAllStatus();

    public ProductVariantResponse getProductVariant(UUID productId, UUID productVariantId);
}
