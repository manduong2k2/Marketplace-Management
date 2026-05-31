package com.StoreManagement.Catalog.Domain.Contract;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.StoreManagement.Catalog.Domain.Models.Category;

public interface ICategoryRepository {
    Category save(Category Category);

    List<Category> findAll();

    Optional<Category> findById(UUID id);

    Optional<Category> findByName(String name);

    Category update(Category Category);

    void delete(UUID id);
}
