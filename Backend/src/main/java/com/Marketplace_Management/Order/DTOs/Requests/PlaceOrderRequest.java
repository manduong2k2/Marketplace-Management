package com.Marketplace_Management.Order.DTOs.Requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlaceOrderRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Phone is required")
    private String phone;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    private String note;
}
