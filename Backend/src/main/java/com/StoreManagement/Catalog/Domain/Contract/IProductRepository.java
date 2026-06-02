package com.StoreManagement.Catalog.Domain.Contract;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.StoreManagement.Catalog.Application.DTO.Commands.Product.GetListProductCommand;
import com.StoreManagement.Catalog.Application.DTO.Response.PaginatedResponse;
import com.StoreManagement.Catalog.Domain.Models.Product;


public interface IProductRepository {
    Product save(Product Product);

    PaginatedResponse<Product> findAll(GetListProductCommand command);

    Optional<Product> findById(UUID id);

    List<Product> findByName(String name);

    Product update(Product Product);

    void delete(UUID id);
}
