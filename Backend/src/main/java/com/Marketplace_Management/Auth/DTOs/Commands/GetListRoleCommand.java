package com.Marketplace_Management.Auth.DTOs.Commands;

import com.Marketplace_Management.Auth.DTOs.Request.GetListRoleRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListRoleCommand {
    private int page;
    private int size;
    private String sortBy;
    private String sortOrder;
    private String search;

    public static GetListRoleCommand fromRequest(GetListRoleRequest request) {
        String search = request.getSearch();
        return new GetListRoleCommand(
            request.getPage(),
            request.getSize(),
            request.getSortBy(),
            request.getSortOrder(),
            search == null || search.isBlank() ? null : search.trim()
        );
    }
}
