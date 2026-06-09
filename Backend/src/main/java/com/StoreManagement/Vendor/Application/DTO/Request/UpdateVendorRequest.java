package com.StoreManagement.Vendor.Application.DTO.Request;

import java.util.UUID;

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
public class UpdateVendorRequest {
    @Nullable
    private UUID userId;

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
