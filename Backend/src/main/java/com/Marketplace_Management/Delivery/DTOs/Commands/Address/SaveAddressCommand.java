package com.Marketplace_Management.Delivery.DTOs.Commands.Address;

import com.Marketplace_Management.Delivery.DTOs.Requests.Address.SaveAddressRequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaveAddressCommand {
    private String wardId;
    private String detail;
    private Boolean isDefault;

    public static SaveAddressCommand fromRequest(SaveAddressRequest request) {
        return SaveAddressCommand.builder()
                .wardId(request.getWardId())
                .detail(request.getDetail().trim())
                .isDefault(request.getIsDefault())
                .build();
    }
}
