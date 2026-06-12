package com.Marketplace_Management.Vendor.Application.DTO.Command;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

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
}
