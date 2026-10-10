package com.Marketplace_Management.Order.DTOs.Requests;

import java.time.LocalDateTime;

import com.Marketplace_Management.Shared.DTOs.Requests.PageRequest;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListOrderRequest extends PageRequest {
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

    public ListOrderRequest() {
        super("createdAt", "desc", 10);
    }
}
