package com.Marketplace_Management.Delivery.DTOs.Requests.Address;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteAddressRequest {

    @NotNull(message = "addressId must not be null")
    private Long addressId;
}
