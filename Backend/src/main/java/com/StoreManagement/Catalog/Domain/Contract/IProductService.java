package com.StoreManagement.Catalog.Domain.Contract;

import java.util.List;
import java.util.UUID;

import com.StoreManagement.Catalog.Application.DTO.Commands.Product.CreateProductCommand;
import com.StoreManagement.Catalog.Application.DTO.Commands.Product.UpdateProductCommand;
import com.StoreManagement.Catalog.Application.DTO.Response.ProductResponse;

import java.io.IOException;

public interface IProductService {
    public List<ProductResponse> getAllProducts();

    public ProductResponse createProduct(CreateProductCommand command) throws IOException;

    public ProductResponse getProduct(UUID id);

    public ProductResponse updateProduct(UUID id, UpdateProductCommand command) throws IOException;

    public void deleteProduct(UUID id);
    
    public List<String> getAllStatus();
}
