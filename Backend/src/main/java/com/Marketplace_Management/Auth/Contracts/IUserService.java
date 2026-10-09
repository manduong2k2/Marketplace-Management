package com.Marketplace_Management.Auth.Contracts;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Auth.DTOs.Commands.CreateUserCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.GetListUserCommand;
import com.Marketplace_Management.Auth.DTOs.Commands.UpdateUserCommand;
import com.Marketplace_Management.Auth.DTOs.Response.UserResponse;
import com.Marketplace_Management.Shared.DTOs.Responses.PaginatedResponse;

public interface IUserService {
    PaginatedResponse<UserResponse> getUsers(GetListUserCommand command);
    UserResponse getUser(UUID id);
    UserResponse createUser(CreateUserCommand command);
    UserResponse updateUser(UUID id, UpdateUserCommand command);
    void deleteUser(UUID id, UUID currentUserId);
    int grantRole(UUID roleId, List<UUID> userIds);
    int revokeRole(UUID roleId, List<UUID> userIds, UUID currentUserId);
}
