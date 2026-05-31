package com.StoreManagement.Catalog.Application.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.StoreManagement.Catalog.Application.DTO.Requests.Category.CreateCategoryRequest;
import com.StoreManagement.Catalog.Application.DTO.Requests.Category.UpdateCategoryRequest;
import com.StoreManagement.Catalog.Application.DTO.Response.CategoryResponse;
import com.StoreManagement.Catalog.Domain.Contract.ICategoryRepository;
import com.StoreManagement.Catalog.Domain.Contract.ICategoryService;
import com.StoreManagement.Catalog.Domain.Models.Category;
import com.StoreManagement.Shared.Domain.Contracts.IEventPublisher;
import com.StoreManagement.Shared.Infrastructure.Event.EventOptions;

import jakarta.transaction.Transactional;

@Service
public class CategoryService implements ICategoryService {
    @Autowired
    public ICategoryRepository CategoryRepository;
    @Autowired
    public IEventPublisher eventPublisher; 

    public List<CategoryResponse> getAllCategories() {
        return CategoryRepository.findAll().stream()
                .map(CategoryResponse::new)
                .toList();
    }

    public CategoryResponse getCategory(UUID CategoryId) {
        return CategoryRepository.findById(CategoryId)
                .map(CategoryResponse::new)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category Category = new Category(
                null,
                request.getName(),
                request.getParentId(),
                request.getImage(),
                request.getDescription()
        );

        Category = CategoryRepository.save(Category);

        publishDomainEvents(Category, "Category.created");

        return new CategoryResponse(Category);
    }

    @Transactional
    public CategoryResponse updateCategory(UUID CategoryId, UpdateCategoryRequest request) {
        Category Category = CategoryRepository.findById(CategoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Category.setName(request.getName());
        Category.setDescription(request.getDescription());

        Category = CategoryRepository.save(Category);

        publishDomainEvents(Category, "Category.updated");
        
        return new CategoryResponse(Category);
    }

    @Transactional
    public void deleteCategory(UUID CategoryId) {
        CategoryRepository.delete(CategoryId);
    }

    @Async
    private void publishDomainEvents(Category Category, String queue) {
        Category.getDomainEvents()
                .forEach(event -> eventPublisher.publish(event, new EventOptions(queue, false)));

        Category.clearDomainEvents();
    }
}
