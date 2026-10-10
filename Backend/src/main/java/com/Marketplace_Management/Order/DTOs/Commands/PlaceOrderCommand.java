package com.Marketplace_Management.Order.DTOs.Commands;

import java.util.UUID;

import com.Marketplace_Management.Order.DTOs.Requests.PlaceOrderRequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaceOrderCommand {
    private UUID cartId;
    private UUID userId;
    private String name;
    private String phone;
    private String address;
    private String note;

    public static PlaceOrderCommand fromRequest(PlaceOrderRequest request) {
        return PlaceOrderCommand.builder()
                .name(request.getName().trim())
                .phone(request.getPhone().trim())
                .address(request.getAddress().trim())
                .note(request.getNote() != null && !request.getNote().isBlank() ? request.getNote().trim() : null)
                .build();
    }
}
