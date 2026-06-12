package com.Marketplace_Management.Catalog.Domain.Contract;

import java.util.Optional;
import java.util.UUID;

import com.Marketplace_Management.Catalog.Application.DTO.Commands.Brand.GetListBrandCommand;
import com.Marketplace_Management.Catalog.Domain.Models.Brand;
import com.Marketplace_Management.Shared.Application.DTO.Responses.PaginatedResponse;

public interface IBrandRepository {
    Brand save(Brand brand);

    PaginatedResponse<Brand> findAll(GetListBrandCommand command);

    Optional<Brand> findById(UUID id);

    Optional<Brand> findByName(String name);

    Brand update(Brand brand);

    void delete(UUID id);
}
