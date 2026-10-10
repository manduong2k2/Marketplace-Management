package com.Marketplace_Management.Shared.DTOs.Requests;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Paging / sorting / search query parameters shared by every list endpoint. Each list request sets its
 * defaults in its constructor and can add rules by overriding a getter (e.g. the allowed sortBy values).
 */
@Data
public abstract class PageRequest {
    @Min(value = 0, message = "page must be >= 0")
    private int page = 0;

    @Min(value = 1, message = "size must be >= 1")
    private int size;

    private String sortBy;

    @Pattern(regexp = "(?i)asc|desc", message = "sortOrder must be asc or desc")
    private String sortOrder;

    @Nullable
    @Size(max = 100, message = "Search query must not exceed 100 characters")
    private String search;

    protected PageRequest(String defaultSortBy, String defaultSortOrder, int defaultSize) {
        this.sortBy = defaultSortBy;
        this.sortOrder = defaultSortOrder;
        this.size = defaultSize;
    }
}
