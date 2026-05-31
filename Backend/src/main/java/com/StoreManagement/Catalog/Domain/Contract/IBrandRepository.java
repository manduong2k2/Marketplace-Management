package com.StoreManagement.Catalog.Domain.Contract;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.StoreManagement.Catalog.Domain.Models.Brand;

public interface IBrandRepository {
    Brand save(Brand brand);

    List<Brand> findAll();

    Optional<Brand> findById(UUID id);

    Optional<Brand> findByName(String name);

    Brand update(Brand brand);

    void delete(UUID id);
}
