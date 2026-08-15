package com.Marketplace_Management.Delivery.DTOs.Requests.Address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetMyAddressesRequest {
    private String search;
    private String province;
    private String ward;
    private Boolean isDefault;
}
