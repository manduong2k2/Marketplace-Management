package com.Marketplace_Management.Vendor.Security;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Shared.Security.SecurityUtils;
import com.Marketplace_Management.Vendor.Contracts.IVendorService;

@Component
public class VendorSecurity {
    private final IVendorService vendorService;
    
    public VendorSecurity(IVendorService vendorService) {
        this.vendorService = vendorService;
    }
    
    public boolean canCreate() {
        return true;
    }

    public boolean canUpdate(UUID vendorId) {
        var vendor = vendorService.getById(vendorId);
        return vendor != null && vendor.getUserId().equals(SecurityUtils.currentUserId());
    }

    public boolean canDelete(UUID vendorId) {
        var vendor = vendorService.getById(vendorId);
        return vendor != null && vendor.getUserId().equals(SecurityUtils.currentUserId());
    }
}
