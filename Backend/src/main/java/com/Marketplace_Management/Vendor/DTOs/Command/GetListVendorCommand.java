package com.Marketplace_Management.Vendor.DTOs.Command;

import com.Marketplace_Management.Shared.DTOs.Commands.BaseCommand;
import com.Marketplace_Management.Vendor.DTOs.Request.GetListVendorRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor 
@AllArgsConstructor 
public class GetListVendorCommand extends BaseCommand {
    private int page = 0;
    private int size = 10;
    private String sortBy = "name";
    private String sortOrder = "asc";
    private String search;

    public static GetListVendorCommand fromRequest(GetListVendorRequest request) {
        return new GetListVendorCommand(
            request.getPage(),
            request.getSize(),
            request.getSortBy(),
            request.getSortOrder(),
            request.getSearch()
        );
    }
}
