package com.Marketplace_Management.Vendor.DTOs.Command;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.Marketplace_Management.Vendor.DTOs.Request.RegisterVendorRequest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterVendorCommand {
    private String name;
    private String description;
    private MultipartFile logo;
    private MultipartFile banner;
    private String taxCode;
    private String email;
    private UUID addressId;
    private String phone;

    public static RegisterVendorCommand fromRequest(RegisterVendorRequest request) {
        return new RegisterVendorCommand(
            request.getName(),
            request.getDescription(),
            null,
            null,
            request.getTaxCode(),
            request.getEmail(),
            request.getAddressId() != null ? UUID.fromString(request.getAddressId()) : null,
            request.getPhone()
        );
    }
}
