package com.Marketplace_Management.Catalog.Services;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.Marketplace_Management.Catalog.Contracts.ICategoryRepository;
import com.Marketplace_Management.Catalog.Contracts.ICategoryService;
import com.Marketplace_Management.Catalog.DTOs.Commands.Category.CreateCategoryCommand;
import com.Marketplace_Management.Catalog.DTOs.Commands.Category.GetListCategoryCommand;
import com.Marketplace_Management.Catalog.DTOs.Commands.Category.UpdateCategoryCommand;
import com.Marketplace_Management.Catalog.DTOs.Response.CategoryResponse;
import com.Marketplace_Management.Catalog.Models.Category;
import com.Marketplace_Management.Shared.Contracts.IEventPublisher;
import com.Marketplace_Management.Shared.Contracts.IFileService;
import com.Marketplace_Management.Shared.DTOs.Responses.PaginatedResponse;
import com.Marketplace_Management.Shared.Events.EventOptions;

import jakarta.transaction.Transactional;

@Service
public class CategoryService implements ICategoryService {
    private final ICategoryRepository categoryRepository;
    private final IEventPublisher eventPublisher;
    private final IFileService fileService;

    @Value("${spring.application.base-url:http://localhost:8080}")
    private String baseUrl;

    public CategoryService(ICategoryRepository categoryRepository, IEventPublisher eventPublisher, IFileService fileService) {
        this.categoryRepository = categoryRepository;
        this.eventPublisher = eventPublisher;
        this.fileService = fileService;
    } 

    @Cacheable(value = "categories", key = "#command.page + '_' + #command.size + '_' + #command.search")
    public PaginatedResponse<CategoryResponse> getAllCategories(GetListCategoryCommand command) {
        PaginatedResponse<Category> categories = categoryRepository.findAll(command);
        List<CategoryResponse> categoryResponses = categories.getData().stream()
                .map(category -> new CategoryResponse(category, baseUrl))
                .toList();
        return new PaginatedResponse<>(
                categoryResponses,
                categories.getCurrentPage(),
                categories.getPageSize(),
                categories.getTotalElements()
        );
    }

    public CategoryResponse getCategory(UUID CategoryId) {
        return categoryRepository.findById(CategoryId)
                .map(category -> new CategoryResponse(category, baseUrl))
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Transactional
    public CategoryResponse createCategory(CreateCategoryCommand command) throws IOException {
        Category category = new Category(
                null,
                command.getName(),
                null,
                command.getDescription(),
                null,
                null
        );

        category.setParent(command.getParentId() != null ? categoryRepository.findById(command.getParentId()).orElse(null) : null);

        category = categoryRepository.save(category);

        if(command.getImage() != null) {
            String imageUrl = fileService.uploadFile(command.getImage(), "catalog/categories/");
            category.setImage(imageUrl);
            category = categoryRepository.save(category);
        }

        publishDomainEvents(category, "Category.created");

        return new CategoryResponse(category, baseUrl);
    }

    @Transactional
    public CategoryResponse updateCategory(UUID categoryId, UpdateCategoryCommand command) throws IOException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(command.getName());
        category.setDescription(command.getDescription());
        category.setParent(command.getParentId() != null ? categoryRepository.findById(command.getParentId()).orElse(null) : null);

        validateCircularReference(categoryId, command.getParentId());

        if(command.getImage() != null) {
            String currentImage = category.getImage();
            String imageUrl = fileService.uploadFile(command.getImage(), "catalog/categories/");
            category.setImage(imageUrl);
            if(currentImage != null) {
                fileService.deleteFile(currentImage);
            }
        }

        category = categoryRepository.save(category);

        publishDomainEvents(category, "Category.updated");
        
        return new CategoryResponse(category, baseUrl);
    }

    private void validateCircularReference(UUID categoryId, UUID parentId) {
        if(parentId == null) {
            return;
        }

        if(categoryRepository.findById(parentId).get().getParent() != null && categoryRepository.findById(parentId).get().getParent().getId() != null) {
            validateCircularReference(categoryId, categoryRepository.findById(parentId).get().getParent().getId());
        }
        
        if(categoryRepository.findById(parentId).get().getId().equals(categoryId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot set parent to its descendants");
        }
    }

    @Transactional
    public void deleteCategory(UUID categoryId) {
        categoryRepository.delete(categoryId);
    }

    @Async
    private void publishDomainEvents(Category category, String queue) {
        category.getDomainEvents()
                .forEach(event -> eventPublisher.publish(event, new EventOptions(queue, false)));

        category.clearDomainEvents();
    }
}
