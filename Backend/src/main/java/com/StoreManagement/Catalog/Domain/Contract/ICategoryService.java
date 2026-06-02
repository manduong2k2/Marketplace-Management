package com.StoreManagement.Catalog.Domain.Contract;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.StoreManagement.Catalog.Application.DTO.Commands.Category.CreateCategoryCommand;
import com.StoreManagement.Catalog.Application.DTO.Commands.Category.UpdateCategoryCommand;
import com.StoreManagement.Catalog.Application.DTO.Response.CategoryResponse;

public interface ICategoryService {
    public List<CategoryResponse> getAllCategories();

    public CategoryResponse createCategory(CreateCategoryCommand command) throws IOException;

    public CategoryResponse getCategory(UUID id);

    public CategoryResponse updateCategory(UUID id, UpdateCategoryCommand command) throws IOException;

    public void deleteCategory(UUID id);

}
