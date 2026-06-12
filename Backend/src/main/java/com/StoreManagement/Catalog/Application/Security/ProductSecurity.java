package com.StoreManagement.Catalog.Application.Security;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.StoreManagement.Catalog.Domain.Contract.IProductRepository;
import com.StoreManagement.Catalog.Domain.Models.Product;
import com.StoreManagement.Shared.Infrastructure.Security.SecurityUtils;
import com.StoreManagement.Vendor.Application.DTO.Response.VendorResponse;
import com.StoreManagement.Vendor.Domain.Contract.IVendorService;

@Component
public class ProductSecurity {
    private final IProductRepository repository;
    private final IVendorService vendorService;

    public ProductSecurity(IProductRepository repository, IVendorService vendorService) {
        this.repository = repository;
        this.vendorService = vendorService;
    }

    public boolean canView(Long productId) {
        return true;
    }

    public boolean canCreate() {
        List<String> roles = SecurityUtils.currentUserRoles();

        VendorResponse vendor = vendorService.getByUser(SecurityUtils.currentUserId());
        if (vendor != null) {
            return true;
        }
        
        return roles.contains("ADMIN");
    }

    public boolean canUpdate(UUID productId) {
        List<String> roles = SecurityUtils.currentUserRoles();

        Product product = repository.findById(productId);
        if (product != null) {
            return true;
        }
        
        return roles.contains("ADMIN");
    }
}
