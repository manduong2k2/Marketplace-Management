package com.Marketplace_Management.Catalog.Application.DTO.Commands.Product;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Catalog.Application.DTO.Requests.Product.GetListProductRequest;
import com.Marketplace_Management.Shared.Application.DTO.Commands.BaseCommand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
    
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetListProductCommand extends BaseCommand {
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
            BaseCommand.safeTrim(request.getSortBy()),
            BaseCommand.safeTrim(request.getSortOrder()),
            BaseCommand.safeTrim(request.getSearch()),
            request.getCategoryIds() != null ? request.getCategoryIds().stream().map(UUID::fromString).toList() : null,
            request.getBrandId() != null ? UUID.fromString(request.getBrandId()) : null
        );
    }
}
