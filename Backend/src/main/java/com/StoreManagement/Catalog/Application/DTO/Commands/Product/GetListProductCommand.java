package com.StoreManagement.Catalog.Application.DTO.Commands.Product;

import java.util.List;
import java.util.UUID;

import com.StoreManagement.Catalog.Application.DTO.Requests.Product.GetListProductRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListProductCommand {
    private int page = 0;
    private int size = 10;
    private String sortBy = "name";
    private String sortOrder = "asc";
    private String search;
    private List<UUID> categoryIds;
    private UUID brandId;

    public static GetListProductCommand fromRequest(GetListProductRequest request) {
        return new GetListProductCommand(
            request.getPage(),
            request.getSize(),
            request.getSortBy(),
            request.getSortOrder(),
            request.getSearch(),
            request.getCategoryIds(),
            request.getBrandId()
        );
    }
}
