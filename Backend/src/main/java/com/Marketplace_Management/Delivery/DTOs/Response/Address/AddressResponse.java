package com.Marketplace_Management.Delivery.DTOs.Response.Address;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonPropertyOrder({"userId", "ward", "province", "streetName", "houseNumber", "detail"})
@Builder
public class AddressResponse {
    private Long    id;
    private UUID    userId;
    private String  title;
    private String  ward;
    private String  province;
    private String  streetName;
    private String  houseNumber;
    private String  detail;
    private Boolean isDefault;
}
