package com.StoreManagement.Catalog.Domain.Contract;

import java.util.List;
import java.util.UUID;

import com.StoreManagement.Catalog.Application.DTO.Commands.Product.CreateProductCommand;
import com.StoreManagement.Catalog.Application.DTO.Commands.Product.GetListProductCommand;
import com.StoreManagement.Catalog.Application.DTO.Commands.Product.UpdateProductCommand;
import com.StoreManagement.Catalog.Application.DTO.Response.ProductResponse;
import com.StoreManagement.Catalog.Application.DTO.Response.ProductVariantResponse;
import com.StoreManagement.Shared.Application.DTO.Responses.PaginatedResponse;

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
