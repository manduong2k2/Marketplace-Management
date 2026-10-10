package com.Marketplace_Management.Auth.DTOs.Commands;

import java.util.UUID;

import com.Marketplace_Management.Auth.DTOs.Request.GetListUserRequest;
import com.Marketplace_Management.Shared.DTOs.Commands.PageCommand;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetListUserCommand extends PageCommand {
    private UUID roleId;
    private String status;

    public static GetListUserCommand fromRequest(GetListUserRequest request) {
        return forRole(request.getRoleId(), request);
    }

    /** Users of one role: same paging/search options, role fixed by the path. */
    public static GetListUserCommand forRole(UUID roleId, GetListUserRequest request) {
        return GetListUserCommand.builder()
                .paging(request)
                .roleId(roleId)
                .status(blankToNull(request.getStatus()))
                .build();
    }
}
