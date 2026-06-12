package com.Marketplace_Management.Catalog.Application.Controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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

import com.Marketplace_Management.Catalog.Application.DTO.Commands.Product.CreateProductCommand;
import com.Marketplace_Management.Catalog.Application.DTO.Commands.Product.GetListProductCommand;
import com.Marketplace_Management.Catalog.Application.DTO.Commands.Product.UpdateProductCommand;
import com.Marketplace_Management.Catalog.Application.DTO.Requests.Product.CreateProductRequest;
import com.Marketplace_Management.Catalog.Application.DTO.Requests.Product.GetListProductRequest;
import com.Marketplace_Management.Catalog.Application.DTO.Requests.Product.UpdateProductRequest;
import com.Marketplace_Management.Catalog.Application.DTO.Response.ProductResponse;
import com.Marketplace_Management.Catalog.Application.DTO.Response.ProductVariantResponse;
import com.Marketplace_Management.Catalog.Domain.Contract.IProductService;
import com.Marketplace_Management.Shared.Application.Controllers.BaseController;
import com.Marketplace_Management.Shared.Application.DTO.Responses.PaginatedResponse;
import com.Marketplace_Management.Shared.Domain.Constants.UserRole;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController extends BaseController{
    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Map<String,Object>> getAll(@Valid @ModelAttribute GetListProductRequest request) {
        GetListProductCommand command = GetListProductCommand.fromRequest(request);
        PaginatedResponse<ProductResponse> products = productService.getAllProducts(command);

        return paginatedResponse(products);
    }

    @GetMapping("/statuses")
    public ResponseEntity<Map<String,Object>> getAllStatus() {        
        List<String> statuses = productService.getAllStatus();

        return objectResponse(statuses);
    }

    @PreAuthorize("hasAuthority('" + UserRole.ADMIN + "')")
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @ModelAttribute CreateProductRequest request) throws IOException {
        CreateProductCommand command = CreateProductCommand.fromRequest(request);

        ProductResponse productRes = productService.createProduct(command);

        return createdResponse(productRes);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> details(@PathVariable UUID productId) {
        ProductResponse product = productService.getProduct(productId);
        
        return objectResponse(product);
    }

    @GetMapping("/{productId}/variants")
    public ResponseEntity<Map<String, Object>> getProductVariants(@PathVariable UUID productId) {
        return simpleResponse(null);
    }
    

    @GetMapping("/{productId}/variants/{productVariantId}")
    public ResponseEntity<Map<String, Object>> getProductVariant(@PathVariable UUID productId, @PathVariable UUID productVariantId) {
        ProductVariantResponse productVariant = productService.getProductVariant(productId, productVariantId);
        
        return objectResponse(productVariant);
    }
    
    @PreAuthorize("hasAuthority('" + UserRole.ADMIN + "')")
    @PutMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> update(
        @PathVariable UUID productId, 
        @Valid @ModelAttribute UpdateProductRequest request
    ) throws IOException {
        UpdateProductCommand command = UpdateProductCommand.fromRequest(request);
        ProductResponse updated = productService.updateProduct(productId, command);
        
        return objectResponse(updated);
    }

    @PreAuthorize("hasAuthority('" + UserRole.ADMIN + "')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        
        return simpleResponse("Product deleted successfully");
    }
}