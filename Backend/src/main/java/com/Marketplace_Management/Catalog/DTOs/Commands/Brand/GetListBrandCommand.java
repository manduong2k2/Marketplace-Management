package com.Marketplace_Management.Catalog.DTOs.Commands.Brand;

import com.Marketplace_Management.Catalog.DTOs.Requests.Brand.GetListBrandRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListBrandCommand {
    private int page = 0;
    private int size = 10;
    private String sortBy = "name";
    private String sortOrder = "asc";

    private String search;

    public static GetListBrandCommand fromRequest(GetListBrandRequest request) {
        return new GetListBrandCommand(
            request.getPage(),
            request.getSize(),
            request.getSortBy(),
            request.getSortOrder(),
            request.getSearch()
        );
    }
}
