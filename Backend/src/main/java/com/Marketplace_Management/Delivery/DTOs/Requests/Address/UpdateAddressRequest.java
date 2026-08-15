package com.Marketplace_Management.Delivery.DTOs.Requests.Address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAddressRequest {
    @NotBlank
    private String streetName;
    
    @NotBlank
    private String houseNumber;
    
    private String title;
    
    private String detail;
    
    @NotNull
    private String wardId;
    
    @NotNull
    private Boolean isDefault;
}
