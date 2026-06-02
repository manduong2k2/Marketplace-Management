package com.StoreManagement.Catalog.Application.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.StoreManagement.Catalog.Application.DTO.Commands.Product.CreateProductCommand;
import com.StoreManagement.Catalog.Application.DTO.Commands.Product.GetListProductCommand;
import com.StoreManagement.Catalog.Application.DTO.Commands.Product.UpdateProductCommand;
import com.StoreManagement.Catalog.Application.DTO.Requests.Product.CreateProductRequest;
import com.StoreManagement.Catalog.Application.DTO.Requests.Product.GetListProductRequest;
import com.StoreManagement.Catalog.Application.DTO.Requests.Product.UpdateProductRequest;
import com.StoreManagement.Catalog.Application.DTO.Response.PaginatedResponse;
import com.StoreManagement.Catalog.Application.DTO.Response.ProductResponse;
import com.StoreManagement.Catalog.Domain.Contract.IProductService;
import com.StoreManagement.Shared.Domain.Constants.UserRole;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private IProductService productService;

    @GetMapping
    public ResponseEntity<HashMap<String,Object>> getAll(@Valid @ModelAttribute GetListProductRequest request) {
        GetListProductCommand command = GetListProductCommand.fromRequest(request);
        PaginatedResponse<ProductResponse> products = productService.getAllProducts(command);

        HashMap<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", products.getData());
        response.put("pagination", new HashMap<String, Object>() {{
            put("currentPage", products.getCurrentPage());
            put("pageSize", products.getPageSize());
            put("totalElements", products.getTotalElements());
            put("totalPages", products.getTotalPages());
            put("hasNext", products.isHasNext());
            put("hasPrevious", products.isHasPrevious());
        }});
        
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/statuses")
    public ResponseEntity<HashMap<String,Object>> getAllStatus() {        
        List<String> statuses = productService.getAllStatus();

        HashMap<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data",  statuses);
        
        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("hasAuthority('" + UserRole.ADMIN + "')")
    @PostMapping
    public ResponseEntity<HashMap<String, Object>> create(@Valid @ModelAttribute CreateProductRequest request) throws IOException {
        CreateProductCommand command = CreateProductCommand.fromRequest(request);

        ProductResponse productRes = productService.createProduct(command);

        HashMap<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", productRes);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<HashMap<String, Object>> details(@PathVariable UUID productId) {
        ProductResponse product = productService.getProduct(productId);
        
        HashMap<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", product);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    
    @PreAuthorize("hasAuthority('" + UserRole.ADMIN + "')")
    @PutMapping("/{productId}")
    public ResponseEntity<HashMap<String, Object>> update(
        @PathVariable UUID productId, 
        @Valid @ModelAttribute UpdateProductRequest request
    ) throws IOException {
        UpdateProductCommand command = UpdateProductCommand.fromRequest(request);
        ProductResponse updated = productService.updateProduct(productId, command);
        
        HashMap<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", updated);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PreAuthorize("hasAuthority('" + UserRole.ADMIN + "')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<HashMap<String, Object>> delete(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        
        HashMap<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Product deleted successfully");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}