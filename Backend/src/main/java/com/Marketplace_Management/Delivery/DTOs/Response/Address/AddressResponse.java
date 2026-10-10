package com.Marketplace_Management.Delivery.DTOs.Response.Address;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {
    private Long id;
    private String detail;
    private Boolean isDefault;
    private RegionResponse ward;
    private RegionResponse province;
    /** "detail, Phường X, Thành phố Y" */
    private String fullAddress;
}
