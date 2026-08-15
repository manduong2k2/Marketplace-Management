package com.Marketplace_Management.Delivery.DTOs.Commands.Address;

import com.Marketplace_Management.Delivery.DTOs.Requests.Address.CreateAddressRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAddressCommand {
    @NotBlank
    private String title;
    
    @NotBlank
    private String streetName;
    
    @NotBlank
    private String houseNumber;
    
    private String detail;
    
    @NotBlank
    private String wardId;
    
    private Boolean isDefault;

    public static CreateAddressCommand fromRequest(CreateAddressRequest request) {
        return CreateAddressCommand.builder()
            .title(request.getTitle())
            .streetName(request.getStreetName())
            .houseNumber(request.getHouseNumber())
            .detail(request.getDetail())
            .wardId(request.getWardId())
            .isDefault(request.getIsDefault())
            .build();
    }
}
