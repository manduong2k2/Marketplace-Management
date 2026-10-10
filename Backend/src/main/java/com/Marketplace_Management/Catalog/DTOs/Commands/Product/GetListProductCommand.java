package com.Marketplace_Management.Catalog.DTOs.Commands.Product;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Catalog.DTOs.Requests.Product.GetListProductRequest;
import com.Marketplace_Management.Shared.DTOs.Commands.PageCommand;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetListProductCommand extends PageCommand {
    private List<UUID> categoryIds;
    private UUID brandId;
    private UUID vendorId;

    public static GetListProductCommand fromRequest(GetListProductRequest request) {
        return GetListProductCommand.builder()
                .paging(request)
                .categoryIds(request.getCategoryIds() != null ? request.getCategoryIds().stream().map(UUID::fromString).toList() : List.of())
                .brandId(uuidOrNull(request.getBrandId()))
                .vendorId(uuidOrNull(request.getVendorId()))
                .build();
    }
}
