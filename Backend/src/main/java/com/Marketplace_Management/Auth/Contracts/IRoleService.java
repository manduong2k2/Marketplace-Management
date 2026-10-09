package com.Marketplace_Management.Auth.Contracts;

import java.util.UUID;

import com.Marketplace_Management.Auth.DTOs.Commands.GetListRoleCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.GetListUserCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.SaveRoleCommand;
import com.Marketplace_Management.Auth.DTOs.Response.RoleResponse;
import com.Marketplace_Management.Auth.DTOs.Response.UserResponse;
import com.Marketplace_Management.Shared.DTOs.Responses.PaginatedResponse;

public interface IRoleService {
    PaginatedResponse<RoleResponse> getRoles(GetListRoleCommand command);
    RoleResponse getRole(UUID id);
    PaginatedResponse<UserResponse> getRoleUsers(GetListUserCommand command);
    RoleResponse createRole(SaveRoleCommand command);
    RoleResponse updateRole(UUID id, SaveRoleCommand command);
    void deleteRole(UUID id);
}
