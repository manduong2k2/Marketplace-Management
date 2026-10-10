package com.Marketplace_Management.Catalog.DTOs.Requests.Brand;

import com.Marketplace_Management.Shared.DTOs.Requests.PageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetListBrandRequest extends PageRequest {
    public GetListBrandRequest() {
        super("name", "asc", 10);
    }
}
