package com.Marketplace_Management.Catalog.Contracts;

import java.util.Optional;
import java.util.UUID;

import com.Marketplace_Management.Catalog.DTO.Commands.Category.GetListCategoryCommand;
import com.Marketplace_Management.Catalog.Models.Category;
import com.Marketplace_Management.Shared.Application.DTO.Responses.PaginatedResponse;

public interface ICategoryRepository {
    Category save(Category Category);

    PaginatedResponse<Category> findAll(GetListCategoryCommand command);

    Optional<Category> findById(UUID id);

    Optional<Category> findByName(String name);

    Category update(Category Category);

    void delete(UUID id);
}
