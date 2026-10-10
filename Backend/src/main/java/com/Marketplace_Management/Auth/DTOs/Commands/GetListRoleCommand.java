package com.Marketplace_Management.Auth.DTOs.Commands;

import com.Marketplace_Management.Auth.DTOs.Request.GetListRoleRequest;
import com.Marketplace_Management.Shared.DTOs.Commands.PageCommand;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetListRoleCommand extends PageCommand {
    public static GetListRoleCommand fromRequest(GetListRoleRequest request) {
        return GetListRoleCommand.builder().paging(request).build();
    }
}
