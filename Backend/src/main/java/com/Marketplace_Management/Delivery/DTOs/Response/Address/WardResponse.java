package com.Marketplace_Management.Delivery.DTOs.Response.Address;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonPropertyOrder({"id", "name", "fullName"})
public class WardResponse {
    private String id;
    private String name;
    private String fullName;
}
