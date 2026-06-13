package com.Marketplace_Management.Vendor.DTOs.Request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterVendorRequest {
    @NotBlank(message = "Vendor name is required")
    @Size(min = 1, max = 100, message = "Vendor name must be between 1 and 100 characters")
    private String name;

    @Nullable
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Nullable
    private MultipartFile logo;

    @Nullable
    private MultipartFile banner;

    @Nullable
    @NotBlank(message = "Tax code is required")
    @Pattern(regexp = "^[A-Za-z0-9]{5,100}$", message = "Tax code must contain only alphanumeric characters and be 5 to 100 characters long")
    @Size(max = 100, message = "Tax code must not exceed 100 characters")
    private String taxCode;

    @Nullable
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Nullable
    @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$", message = "Address ID must be a valid UUID")
    private String addressId;

    @Nullable
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone number must be valid")
    @Size(max = 15, message = "Phone must not exceed 15 characters")
    private String phone;
}
