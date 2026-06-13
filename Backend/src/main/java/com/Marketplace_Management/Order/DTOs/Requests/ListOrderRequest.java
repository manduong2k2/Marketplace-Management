package com.Marketplace_Management.Order.DTOs.Requests;

import java.time.LocalDateTime;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListOrderRequest {
    private int page = 0;
    private int size = 10;
    private String sortBy = "createdAt";
    private String sortOrder = "desc";

    @Nullable
    @Size(max = 100, message = "Search query must not exceed 100 characters")
    private String search;

    @Nullable
    private String status;

    @Nullable
    private Double totalMin;

    @Nullable
    private Double totalMax;

    @Nullable
    private LocalDateTime dateFrom;

    @Nullable
    private LocalDateTime dateTo;
}
