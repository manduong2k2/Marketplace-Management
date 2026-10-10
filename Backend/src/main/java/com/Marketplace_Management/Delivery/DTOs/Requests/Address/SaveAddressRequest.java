package com.Marketplace_Management.Delivery.DTOs.Requests.Address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Body of POST /api/addresses and PUT /api/addresses/{id}. */
@Data
@NoArgsConstructor
public class SaveAddressRequest {
    @NotBlank(message = "Please choose a ward")
    @Size(max = 20)
    private String wardId;

    @NotBlank(message = "Please enter the address detail")
    @Size(max = 255, message = "Address detail must not exceed 255 characters")
    private String detail;

    /** Optional: null keeps the current state (a new first address always becomes the default). */
    private Boolean isDefault;
}
