package com.StoreManagement.Vendor.Application.DTO.Response;

import java.util.UUID;

import com.StoreManagement.Vendor.Domain.Model.Vendor;
import com.StoreManagement.Vendor.Domain.Model.VendorStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorResponse {
    private UUID id;
    private UUID userId;
    private String name;
    private VendorStatus status;
    private String description;
    private String logo;
    private String banner;
    private String taxCode;
    private String email;
    private UUID addressId;
    private String phone;

    public VendorResponse(Vendor vendor) {
        this.id             = vendor.getId();
        this.userId         = vendor.getUserId();
        this.name           = vendor.getName();
        this.status         = vendor.getStatus();
        this.description    = vendor.getDescription();
        this.logo           = vendor.getLogo();
        this.banner         = vendor.getBanner();
        this.taxCode        = vendor.getTaxCode();
        this.email          = vendor.getEmail();
        this.addressId      = vendor.getAddressId();
        this.phone          = vendor.getPhone();
    }
}
