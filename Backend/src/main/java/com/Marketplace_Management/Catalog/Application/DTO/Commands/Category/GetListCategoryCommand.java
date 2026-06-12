package com.Marketplace_Management.Catalog.Application.DTO.Commands.Category;

import com.Marketplace_Management.Catalog.Application.DTO.Requests.Category.GetListCategoryRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListCategoryCommand {
    private int page = 0;
    private int size = 10;
    private String sortBy = "name";
    private String sortOrder = "asc";
    private String search;

    public static GetListCategoryCommand fromRequest(GetListCategoryRequest request) {
        return new GetListCategoryCommand(
            request.getPage(),
            request.getSize(),
            request.getSortBy(),
            request.getSortOrder(),
            request.getSearch()
        );
    }
}
