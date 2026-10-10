package com.Marketplace_Management.Auth.DTOs.Commands;

import com.Marketplace_Management.Auth.DTOs.Request.RegisterRequest;

import lombok.Data;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import com.Marketplace_Management.Shared.DTOs.Commands.BaseCommand;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class RegisterCommand extends BaseCommand {
    private String email;
    private String password;
    private String name;
    private String phone;

    public static RegisterCommand fromRequest(RegisterRequest request) {
        return RegisterCommand.builder()
            .email(safeTrim(request.getEmail()))
            .password(request.getPassword())
            .name(blankToNull(request.getName()))
            .phone(blankToNull(request.getPhone()))
            .build();
    }
}
