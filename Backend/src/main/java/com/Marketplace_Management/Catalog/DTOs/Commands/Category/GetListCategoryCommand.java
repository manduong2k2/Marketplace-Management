package com.Marketplace_Management.Catalog.DTOs.Commands.Category;

import com.Marketplace_Management.Catalog.DTOs.Requests.Category.GetListCategoryRequest;
import com.Marketplace_Management.Shared.DTOs.Commands.PageCommand;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetListCategoryCommand extends PageCommand {
    public static GetListCategoryCommand fromRequest(GetListCategoryRequest request) {
        return GetListCategoryCommand.builder().paging(request).build();
    }
}
