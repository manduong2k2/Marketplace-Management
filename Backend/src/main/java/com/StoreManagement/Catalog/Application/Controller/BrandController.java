package com.StoreManagement.Catalog.Application.Controller;

import java.io.IOException;
import java.util.HashMap;
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

import com.StoreManagement.Catalog.Application.DTO.Commands.Brand.CreateBrandCommand;
import com.StoreManagement.Catalog.Application.DTO.Commands.Brand.GetListBrandCommand;
import com.StoreManagement.Catalog.Application.DTO.Commands.Brand.UpdateBrandCommand;
import com.StoreManagement.Catalog.Application.DTO.Requests.Brand.CreateBrandRequest;
import com.StoreManagement.Catalog.Application.DTO.Requests.Brand.GetListBrandRequest;
import com.StoreManagement.Catalog.Application.DTO.Requests.Brand.UpdateBrandRequest;
import com.StoreManagement.Catalog.Application.DTO.Response.BrandResponse;
import com.StoreManagement.Catalog.Application.DTO.Response.PaginatedResponse;
import com.StoreManagement.Catalog.Domain.Contract.IBrandService;
import com.StoreManagement.Shared.Domain.Constants.UserRole;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/brands")
public class BrandController {
    @Autowired
    private IBrandService brandService;

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