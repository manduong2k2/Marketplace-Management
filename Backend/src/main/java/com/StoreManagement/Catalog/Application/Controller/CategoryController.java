package com.StoreManagement.Catalog.Application.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.StoreManagement.Catalog.Application.DTO.Requests.Category.CreateCategoryRequest;
import com.StoreManagement.Catalog.Application.DTO.Requests.Category.UpdateCategoryRequest;
import com.StoreManagement.Catalog.Application.DTO.Response.CategoryResponse;
import com.StoreManagement.Catalog.Domain.Contract.ICategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    @GetMapping
    public ResponseEntity<HashMap<String,Object>> getAll() {
        List<CategoryResponse> categories = categoryService.getAllCategories();

        HashMap<String,Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", categories);

        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping
    public ResponseEntity<HashMap<String,Object>> create(@Valid @ModelAttribute CreateCategoryRequest request) {
        try {
            CategoryResponse category = categoryService.createCategory(request);
            
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

    @PreAuthorize("hasAuthority('Admin')")
    @GetMapping("/{categoryId}")
    public ResponseEntity<HashMap<String,Object>> details(@PathVariable UUID categoryId) {
        CategoryResponse category = categoryService.getCategory(categoryId);
        
        HashMap<String,Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", category);
        
        return ResponseEntity.ok().body(response);
    }
    
    @PreAuthorize("hasAuthority('Admin')")
    @PutMapping("/{categoryId}")
    public ResponseEntity<HashMap<String,Object>> update(@PathVariable UUID categoryId, @Valid @ModelAttribute UpdateCategoryRequest request) {
        CategoryResponse category = categoryService.updateCategory(categoryId, request);
        
        HashMap<String,Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", category);
        
        return ResponseEntity.ok().body(response);
    }
    
    @PreAuthorize("hasAuthority('Admin')")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<HashMap<String,Object>> delete(@PathVariable UUID categoryId) {
        categoryService.deleteCategory(categoryId);
        
        HashMap<String,Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Category deleted successfully");
        
        return ResponseEntity.ok().body(response);
    }
}