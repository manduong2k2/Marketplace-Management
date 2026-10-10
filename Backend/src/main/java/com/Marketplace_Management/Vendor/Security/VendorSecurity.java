package com.Marketplace_Management.Vendor.Security;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.Marketplace_Management.Shared.Security.SecurityUtils;
import com.Marketplace_Management.Vendor.Contracts.IVendorService;

/** Used in @PreAuthorize("@vendorSecurity.canManage(#id)"): the vendor's owner or an admin. */
@Component
public class VendorSecurity {
    private final IVendorService vendorService;

    public VendorSecurity(IVendorService vendorService) {
        this.vendorService = vendorService;
    }

    /** Unknown vendor ids fall through to the service, which answers 404. */
    public boolean canManage(UUID vendorId) {
        return SecurityUtils.isOwnerOrAdmin(vendorService.getById(vendorId).getUserId());
    }
}
