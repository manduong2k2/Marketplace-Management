package com.StoreManagement.Catalog.Domain.Contract;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.StoreManagement.Catalog.Application.DTO.Requests.Category.CreateCategoryRequest;
import com.StoreManagement.Catalog.Application.DTO.Requests.Category.UpdateCategoryRequest;
import com.StoreManagement.Catalog.Application.DTO.Response.CategoryResponse;

public interface ICategoryService {
    public List<CategoryResponse> getAllCategories();

    public CategoryResponse createCategory(CreateCategoryRequest request) throws IOException;

    public CategoryResponse getCategory(UUID id);

    public CategoryResponse updateCategory(UUID id, UpdateCategoryRequest request);

    public void deleteCategory(UUID id);

}
