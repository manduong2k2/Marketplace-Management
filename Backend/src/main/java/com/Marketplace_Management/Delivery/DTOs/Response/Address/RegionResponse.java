package com.Marketplace_Management.Delivery.DTOs.Response.Address;

import lombok.Builder;
import lombok.Data;

/** A province or a ward. */
@Data
@Builder
public class RegionResponse {
    private String id;
    private String name;
    private String fullName;
}
