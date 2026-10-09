package com.Marketplace_Management.Auth.DTOs.Commands;

import com.Marketplace_Management.Auth.DTOs.Request.CreateRoleRequest;
import com.Marketplace_Management.Auth.DTOs.Request.UpdateRoleRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Shared by create and update: both take a name and a code. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveRoleCommand {
    private String name;
    private String code;

    public static SaveRoleCommand fromRequest(CreateRoleRequest request) {
        return new SaveRoleCommand(request.getName().trim(), request.getCode());
    }

    public static SaveRoleCommand fromRequest(UpdateRoleRequest request) {
        return new SaveRoleCommand(request.getName().trim(), request.getCode());
    }
}
