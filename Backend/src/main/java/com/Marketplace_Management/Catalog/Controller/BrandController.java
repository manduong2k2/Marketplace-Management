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

import com.Marketplace_Management.Catalog.Contracts.IBrandService;
import com.Marketplace_Management.Catalog.DTOs.Commands.Brand.CreateBrandCommand;
import com.Marketplace_Management.Catalog.DTOs.Commands.Brand.GetListBrandCommand;
import com.Marketplace_Management.Catalog.DTOs.Commands.Brand.UpdateBrandCommand;
import com.Marketplace_Management.Catalog.DTOs.Requests.Brand.CreateBrandRequest;
import com.Marketplace_Management.Catalog.DTOs.Requests.Brand.GetListBrandRequest;
import com.Marketplace_Management.Catalog.DTOs.Requests.Brand.UpdateBrandRequest;
import com.Marketplace_Management.Catalog.DTOs.Response.BrandResponse;
import com.Marketplace_Management.Shared.Constants.UserRole;
import com.Marketplace_Management.Shared.DTOs.Responses.PaginatedResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/brands")
public class BrandController {
    private final IBrandService brandService;

    public BrandController(IBrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<HashMap<String,Object>> getAll(@Valid @ModelAttribute GetListBrandRequest request) {
        GetListBrandCommand command = GetListBrandCommand.fromRequest(request);
        PaginatedResponse<BrandResponse> brands = brandService.getAllBrands(command);

        HashMap<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", brands.getData());
        response.put("pagination", new HashMap<String, Object>() {{
            put("currentPage", brands.getCurrentPage());
            put("pageSize", brands.getPageSize());
            put("totalElements", brands.getTotalElements());
            put("totalPages", brands.getTotalPages());
            put("hasNext", brands.isHasNext());
            put("hasPrevious", brands.isHasPrevious());
        }});
        
        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("hasAuthority('" + UserRole.ADMIN + "')")
    @PostMapping
    public ResponseEntity<HashMap<String,Object>> create(@Valid @ModelAttribute CreateBrandRequest request) {
        try {
            CreateBrandCommand command = CreateBrandCommand.fromRequest(request);
            BrandResponse brand = brandService.createBrand(command);
            
            HashMap<String,Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", brand);
            
            return ResponseEntity.ok().body(response);
        } catch (IOException e) {
            HashMap<String,Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to upload image: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/{brandId}")
    public ResponseEntity<HashMap<String,Object>> details(@PathVariable UUID brandId) {
        BrandResponse brand = brandService.getBrand(brandId);
        
        HashMap<String,Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", brand);
        
        return ResponseEntity.ok().body(response);
    }
    
    @PreAuthorize("hasAuthority('" + UserRole.ADMIN + "')")
    @PutMapping("/{brandId}")
    public ResponseEntity<HashMap<String,Object>> update(
        @PathVariable UUID brandId, 
        @Valid @ModelAttribute UpdateBrandRequest request
    ) throws IOException {
        UpdateBrandCommand command = UpdateBrandCommand.fromRequest(request);
        BrandResponse brand = brandService.updateBrand(brandId, command);
        
        HashMap<String,Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", brand);
        
        return ResponseEntity.ok().body(response);
    }
    
    @PreAuthorize("hasAuthority('" + UserRole.ADMIN + "')")
    @DeleteMapping("/{brandId}")
    public ResponseEntity<HashMap<String,Object>> delete(@PathVariable UUID brandId) {
        brandService.deleteBrand(brandId);
        
        HashMap<String,Object> response = new HashMap<>();
        response.put("success", true);
        
        return ResponseEntity.ok().body(response);
    }
}