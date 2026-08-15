package com.Marketplace_Management.Delivery.DTOs.Commands.Address;

import com.Marketplace_Management.Delivery.DTOs.Requests.Address.GetMyAddressesRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetMyAddressesCommand {
    private String search;
    private String province;
    private String ward;
    private Boolean isDefault;

    public static GetMyAddressesCommand fromRequest(GetMyAddressesRequest request) {
        return new GetMyAddressesCommand(
            request.getSearch(),
            request.getProvince(),
            request.getWard(),
            request.getIsDefault()
        );
    }
}
