package com.Marketplace_Management.Vendor.DTOs.Request;

import com.Marketplace_Management.Shared.DTOs.Requests.PageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetListVendorRequest extends PageRequest {
    public GetListVendorRequest() {
        super("name", "asc", 10);
    }
}
