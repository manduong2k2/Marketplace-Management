package com.Marketplace_Management.Delivery.DTOs.Requests.Address;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAddressRequest {

    @NotBlank(message = "title must not be empty")
    private String title;

    @NotBlank(message = "streetName must not be empty")
    private String streetName;
    
    @NotBlank(message = "houseNumber must not be empty")
    private String houseNumber;
    
    private String detail;
    
    @NotBlank(message = "wardId must not be empty")
    private String wardId;
    
    @Nullable
    private Boolean isDefault;
}
