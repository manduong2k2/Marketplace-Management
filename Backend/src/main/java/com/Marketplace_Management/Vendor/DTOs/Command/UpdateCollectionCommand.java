package com.Marketplace_Management.Vendor.DTOs.Command;

import com.Marketplace_Management.Vendor.DTOs.Request.UpdateCollectionRequest;

import lombok.Data;

@Data
public class UpdateCollectionCommand {
    private String name;
    private Integer displayOrder;

    public static UpdateCollectionCommand fromRequest(UpdateCollectionRequest request) {
        UpdateCollectionCommand command = new UpdateCollectionCommand();
        command.setName(request.getName());
        command.setDisplayOrder(request.getDisplayOrder());
        return command;
    }
}