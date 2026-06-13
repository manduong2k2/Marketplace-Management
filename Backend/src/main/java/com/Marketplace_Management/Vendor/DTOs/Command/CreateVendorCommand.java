package com.Marketplace_Management.Vendor.DTOs.Command;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.Marketplace_Management.Vendor.DTOs.Request.CreateVendorRequest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateVendorCommand {
    private UUID userId;
    private String name;
    private String description;
    private MultipartFile logo;
    private MultipartFile banner;
    private String taxCode;
    private String email;
    private UUID addressId;
    private String phone;

    public static CreateVendorCommand fromRequest(CreateVendorRequest request) {
        return new CreateVendorCommand(
            request.getUserId(),
            request.getName(),
            request.getDescription(),
            request.getLogo(),
            request.getBanner(),
            request.getTaxCode(),
            request.getEmail(),
            request.getAddressId() != null ? UUID.fromString(request.getAddressId()) : null,
            request.getPhone()
        );
    }
}
