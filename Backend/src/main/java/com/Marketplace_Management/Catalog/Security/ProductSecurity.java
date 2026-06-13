package com.Marketplace_Management.Catalog.Security;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Catalog.Contracts.IProductRepository;
import com.Marketplace_Management.Catalog.Models.Product;
import com.Marketplace_Management.Shared.Security.SecurityUtils;
import com.Marketplace_Management.Vendor.Contracts.IVendorService;
import com.Marketplace_Management.Vendor.DTOs.Response.VendorResponse;

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
