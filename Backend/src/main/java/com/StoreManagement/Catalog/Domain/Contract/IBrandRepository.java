package com.StoreManagement.Catalog.Domain.Contract;

import java.util.Optional;
import java.util.UUID;

import com.StoreManagement.Catalog.Application.DTO.Commands.Brand.GetListBrandCommand;
import com.StoreManagement.Catalog.Application.DTO.Response.PaginatedResponse;
import com.StoreManagement.Catalog.Domain.Models.Brand;

public interface IBrandRepository {
    Brand save(Brand brand);

    PaginatedResponse<Brand> findAll(GetListBrandCommand command);

    Optional<Brand> findById(UUID id);

    Optional<Brand> findByName(String name);

    Brand update(Brand brand);

    void delete(UUID id);
}
