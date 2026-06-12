package com.Marketplace_Management.Catalog.Domain.Contract;

import java.io.IOException;
import java.util.UUID;

import com.Marketplace_Management.Catalog.Application.DTO.Commands.Category.CreateCategoryCommand;
import com.Marketplace_Management.Catalog.Application.DTO.Commands.Category.GetListCategoryCommand;
import com.Marketplace_Management.Catalog.Application.DTO.Commands.Category.UpdateCategoryCommand;
import com.Marketplace_Management.Catalog.Application.DTO.Response.CategoryResponse;
import com.Marketplace_Management.Shared.Application.DTO.Responses.PaginatedResponse;

public interface ICategoryService {
    public PaginatedResponse<CategoryResponse> getAllCategories(GetListCategoryCommand command);

    public CategoryResponse createCategory(CreateCategoryCommand command) throws IOException;

    public CategoryResponse getCategory(UUID id);

    public CategoryResponse updateCategory(UUID id, UpdateCategoryCommand command) throws IOException;

    public void deleteCategory(UUID id);

}
