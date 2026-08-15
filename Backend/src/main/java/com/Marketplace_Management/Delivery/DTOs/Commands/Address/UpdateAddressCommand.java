package com.Marketplace_Management.Delivery.DTOs.Commands.Address;

import java.util.UUID;

import com.Marketplace_Management.Delivery.DTOs.Requests.Address.UpdateAddressRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAddressCommand {
    private UUID userId;

    private String streetName;
    
    private String houseNumber;
    
    private String detail;

    private String title;
    
    private String wardId;
    
    private Boolean isDefault;

    public static UpdateAddressCommand fromRequest(UpdateAddressRequest request) {
        return new UpdateAddressCommand(
            null,
            request.getStreetName(),
            request.getHouseNumber(),
            request.getDetail(),
            request.getTitle(),
            request.getWardId(),
            request.getIsDefault()
        );
    }
}
