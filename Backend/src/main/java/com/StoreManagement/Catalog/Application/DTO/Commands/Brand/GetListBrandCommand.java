package com.StoreManagement.Catalog.Application.DTO.Commands.Brand;

import com.StoreManagement.Catalog.Application.DTO.Requests.Brand.GetListBrandRequest;

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
