package com.Marketplace_Management.Auth.DTOs.Commands;

import com.Marketplace_Management.Auth.DTOs.Request.UpdateUserRequest;

import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import com.Marketplace_Management.Shared.DTOs.Commands.BaseCommand;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class UpdateUserCommand extends BaseCommand {
    private String name;
    private String phone;
    private String status;
    private String password; // null = keep current password

    public static UpdateUserCommand fromRequest(UpdateUserRequest request) {
        return UpdateUserCommand.builder()
            .name(blankToNull(request.getName()))
            .phone(blankToNull(request.getPhone()))
            .status(request.getStatus())
            .password(request.getPassword() == null || request.getPassword().isEmpty() ? null : request.getPassword())
            .build();
    }

}
