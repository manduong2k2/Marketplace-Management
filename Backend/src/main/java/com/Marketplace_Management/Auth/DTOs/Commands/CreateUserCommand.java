package com.Marketplace_Management.Auth.DTOs.Commands;

import java.util.List;
import java.util.UUID;

import com.Marketplace_Management.Auth.Constants.UserStatus;
import com.Marketplace_Management.Auth.DTOs.Request.CreateUserRequest;

import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import com.Marketplace_Management.Shared.DTOs.Commands.BaseCommand;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class CreateUserCommand extends BaseCommand {
    private String email;
    private String password;
    private String name;
    private String phone;
    private String status;
    private List<UUID> roleIds;

    public static CreateUserCommand fromRequest(CreateUserRequest request) {
        return CreateUserCommand.builder()
            .email(request.getEmail().trim())
            .password(request.getPassword())
            .name(blankToNull(request.getName()))
            .phone(blankToNull(request.getPhone()))
            .status(request.getStatus() == null ? UserStatus.ACTIVE : request.getStatus())
            .roleIds(request.getRoleIds() == null ? List.of() : request.getRoleIds())
            .build();
    }

}
