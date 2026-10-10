package com.Marketplace_Management.Catalog.DTOs.Commands.Brand;

import com.Marketplace_Management.Catalog.DTOs.Requests.Brand.GetListBrandRequest;
import com.Marketplace_Management.Shared.DTOs.Commands.PageCommand;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetListBrandCommand extends PageCommand {
    public static GetListBrandCommand fromRequest(GetListBrandRequest request) {
        return GetListBrandCommand.builder().paging(request).build();
    }
}
