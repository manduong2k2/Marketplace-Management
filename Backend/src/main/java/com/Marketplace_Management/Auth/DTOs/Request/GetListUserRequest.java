package com.Marketplace_Management.Auth.DTOs.Request;

import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListUserRequest {
    @Min(value = 0, message = "page must be >= 0")
    private int page = 0;

    @Min(value = 1, message = "size must be >= 1")
    @Max(value = 100, message = "size must not exceed 100")
    private int size = 10;

    @Pattern(regexp = "name|email|status|createdAt", message = "sortBy must be one of: name, email, status, createdAt")
    private String sortBy = "createdAt";

    @Pattern(regexp = "(?i)asc|desc", message = "sortOrder must be asc or desc")
    private String sortOrder = "desc";

    // Matches name or email
    @Nullable
    @Size(max = 100, message = "Search query must not exceed 100 characters")
    private String search;

    @Nullable
    private UUID roleId;

    @Nullable
    @Pattern(regexp = "ACTIVE|INACTIVE", message = "status must be ACTIVE or INACTIVE")
    private String status;
}
