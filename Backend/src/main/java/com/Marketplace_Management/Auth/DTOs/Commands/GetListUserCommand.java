package com.Marketplace_Management.Auth.DTOs.Commands;

import java.util.UUID;

import com.Marketplace_Management.Auth.DTOs.Request.GetListUserRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListUserCommand {
    private int page;
    private int size;
    private String sortBy;
    private String sortOrder;
    private String search;
    private UUID roleId;
    private String status;

    public static GetListUserCommand fromRequest(GetListUserRequest request) {
        return new GetListUserCommand(
            request.getPage(),
            request.getSize(),
            request.getSortBy(),
            request.getSortOrder(),
            blankToNull(request.getSearch()),
            request.getRoleId(),
            blankToNull(request.getStatus())
        );
    }

    /** Users of one role: same paging/search options, role fixed by the path. */
    public static GetListUserCommand forRole(UUID roleId, GetListUserRequest request) {
        GetListUserCommand command = fromRequest(request);
        command.setRoleId(roleId);
        return command;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
