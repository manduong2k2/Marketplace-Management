package com.Marketplace_Management.Catalog.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Marketplace_Management.Catalog.Contracts.ICategoryService;
import com.Marketplace_Management.Catalog.DTO.Commands.Category.CreateCategoryCommand;
import com.Marketplace_Management.Catalog.DTO.Commands.Category.GetListCategoryCommand;
import com.Marketplace_Management.Catalog.DTO.Commands.Category.UpdateCategoryCommand;
import com.Marketplace_Management.Catalog.DTO.Requests.Category.CreateCategoryRequest;
import com.Marketplace_Management.Catalog.DTO.Requests.Category.GetListCategoryRequest;
import com.Marketplace_Management.Catalog.DTO.Requests.Category.UpdateCategoryRequest;
import com.Marketplace_Management.Catalog.DTO.Response.CategoryResponse;
import com.Marketplace_Management.Shared.Application.DTO.Responses.PaginatedResponse;
import com.Marketplace_Management.Shared.Domain.Constants.UserRole;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final ICategoryService categoryService;

    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<HashMap<String,Object>> getAll(@Valid @ModelAttribute GetListCategoryRequest request) {
        GetListCategoryCommand command = GetListCategoryCommand.fromRequest(request);
        PaginatedResponse<CategoryResponse> categories = categoryService.getAllCategories(command);

        HashMap<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", categories.getData());
        response.put("pagination", new HashMap<String, Object>() {{
            put("currentPage", categories.getCurrentPage());
            put("pageSize", categories.getPageSize());
            put("totalElements", categories.getTotalElements());
            put("totalPages", categories.getTotalPages());
            put("hasNext", categories.isHasNext());
            put("hasPrevious", categories.isHasPrevious());
        }});

        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("hasAuthority('" + UserRole.ADMIN + "')")
    @PostMapping
    public ResponseEntity<HashMap<String,Object>> create(@Valid @ModelAttribute CreateCategoryRequest request) {
        try {
            CreateCategoryCommand command = CreateCategoryCommand.fromRequest(request);
            CategoryResponse category = categoryService.createCategory(command);
            
            HashMap<String,Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", category);
            
            return ResponseEntity.ok().body(response);
        } catch (IOException e) {
            HashMap<String,Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to upload image: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PreAuthorize("hasAuthority('" + UserRole.ADMIN + "')")
    @GetMapping("/{categoryId}")
    public ResponseEntity<HashMap<String,Object>> details(@PathVariable UUID categoryId) {
        CategoryResponse category = categoryService.getCategory(categoryId);
        
        HashMap<String,Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", category);
        
        return ResponseEntity.ok().body(response);
    }
    
    @PreAuthorize("hasAuthority('" + UserRole.ADMIN + "')")
    @PutMapping("/{categoryId}")
    public ResponseEntity<HashMap<String,Object>> update(
        @PathVariable UUID categoryId, 
        @Valid @ModelAttribute UpdateCategoryRequest request
    ) throws IOException {
        UpdateCategoryCommand command = UpdateCategoryCommand.fromRequest(request);
        CategoryResponse category = categoryService.updateCategory(categoryId, command);
        
        HashMap<String,Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", category);
        
        return ResponseEntity.ok().body(response);
    }
    
    @PreAuthorize("hasAuthority('" + UserRole.ADMIN + "')")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<HashMap<String,Object>> delete(@PathVariable UUID categoryId) {
        categoryService.deleteCategory(categoryId);
        
        HashMap<String,Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Category deleted successfully");
        
        return ResponseEntity.ok().body(response);
    }
}