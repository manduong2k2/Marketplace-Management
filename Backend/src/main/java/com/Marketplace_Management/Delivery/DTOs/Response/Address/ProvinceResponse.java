package com.Marketplace_Management.Delivery.DTOs.Response.Address;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonPropertyOrder({"id", "name", "fullName", "wards"})
@Builder
public class ProvinceResponse {
    private String id;
    private String name;
    private String fullName;
    private List<WardResponse> wards;
}
